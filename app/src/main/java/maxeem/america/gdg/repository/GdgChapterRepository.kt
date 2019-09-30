package maxeem.america.gdg.repository

import android.location.Location
import kotlinx.coroutines.*
import maxeem.america.app
import maxeem.america.gdg.network.GdgApiService
import maxeem.america.gdg.network.GdgChapter
import maxeem.america.gdg.network.GdgResponse
import maxeem.america.gdg.network.LatLong
import maxeem.america.util.thread
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class GdgChapterRepository(private val gdgApiService: GdgApiService) : AnkoLogger {

    class Data(val chapters: List<GdgChapter>, val regions: List<String>)

    companion object {
        private var cachedGdgResponse : GdgResponse? = null
        fun hasCache() = cachedGdgResponse != null
    }

    @Volatile
    private var apiServiceJob : Deferred<GdgResponse>? = null

    private var repoJobInfo = RepoJobInfo(null, null)

    private suspend fun ensureJob(location: LatLong?) : RepoJobInfo {
        val job = repoJobInfo.job
        info("ensureJob, ${job?.isCancelled}, ${repoJobInfo.location}, $job")
        if (job != null && (job.isCompleted || repoJobInfo.location != location))
            repoJobInfo.reset()
        if (repoJobInfo.isEmpty())
            repoJobInfo = startJobAsync(location)
        return repoJobInfo
    }

    suspend fun getData(region: String?, location: LatLong?)
        = ensureJob(location).await().run {
            Data(when (region) {
                null -> chapters
                else -> chaptersByRegion.getValue(region)
            }, regions)
    }

    private suspend fun startJobAsync(location: LatLong? = null) = coroutineScope {
        info("startDataJobAsync, location: $location, apiServiceJob: $apiServiceJob")
        async {
            info("- startDataJobAsync, $this")
            val gdgResponse = queryRepository()
            info("- startDataJobAsync, got response, prepare data")
            withContext(Dispatchers.Default) {
                GdgData.from(gdgResponse, location)
            }
        }.apply {
            invokeOnCompletion {
                info("invokeOnCompletion, isCancelled: $isCancelled, $this")
                if (repoJobInfo.job == this && it is Throwable)
                    repoJobInfo = RepoJobInfo(null, null)
            }
        }
    }.run {
        RepoJobInfo(this, location)
    }

    private suspend fun queryRepository() : GdgResponse {
        val gdgResponse : GdgResponse
        info(" - cachedGdgResponse: $cachedGdgResponse")
        val apiJob = apiServiceJob
        info(" - apiServiceJob, $apiJob")
        if (cachedGdgResponse != null) {
            gdgResponse = cachedGdgResponse!!
        } else {
            info(" - apiJob (apiServiceJob): $apiJob")
            if (apiJob != null && !apiJob.isCompleted) {
                info(" - apiJob: !isComplete, await()")
                gdgResponse = apiJob.await()
            } else {
                info(" - apiJob: make new request, on $thread}")
                gdgResponse = performApiQueryAsync().await()
            }
        }
        return gdgResponse
    }

    private fun performApiQueryAsync() =
        GlobalScope.async {
            info(" - performRepoQuery, on $thread")
            apiServiceJob = this as Deferred<GdgResponse>
            info(" - apiServiceJob set to: $apiServiceJob")
            gdgApiService.getChaptersAsync().apply {
                apiServiceJob = this
                info(" - apiServiceJob re-set to: $apiServiceJob")
            }.await().apply {
                cachedGdgResponse = this
                info(" - j: awaited successfully, clear ref, response: $this")
                apiServiceJob = null
            }
        }

    private class GdgData private constructor(
        val regions: List<String>,
        val chapters: List<GdgChapter>,
        val chaptersByRegion: Map<String, List<GdgChapter>>
    ) {
        companion object {

            fun from(response: GdgResponse, location: LatLong?): GdgData {
                val chapters = response.chapters.sortByDistanceFrom(location)
                return GdgData(response.regions, chapters, chapters.groupBy { it.region })
            }
            private fun List<GdgChapter>.sortByDistanceFrom(location: LatLong?): List<GdgChapter> {
                location ?: return this
                return sortedBy { distanceBetween(it.geo, location)}
            }
            private fun distanceBetween(start: LatLong, location: LatLong): Float {
                val results = FloatArray(3)
                Location.distanceBetween(start.lat, start.lng, location.lat, location.lng, results)
                return results[0]
            }

        }

    }

    private class RepoJobInfo(var job: Deferred<GdgData>?, var location: LatLong?) {

        fun isEmpty() = job == null

        fun reset() {
            job?.takeUnless { it.isCompleted }?.also {
                app.info("cancel() $it")
                it.cancel()
            }
            job = null
            location = null
        }

        suspend fun await() = job!!.await()

    }

}