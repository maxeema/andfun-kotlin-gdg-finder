package maxeem.america.gdg.viewmodels

import android.location.Location
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import maxeem.america.gdg.misc.ApiStatus
import maxeem.america.gdg.network.GdgApi
import maxeem.america.gdg.network.GdgChapter
import maxeem.america.gdg.network.LatLong
import maxeem.america.gdg.repository.GdgChapterRepository
import maxeem.america.util.asImmutable
import maxeem.america.util.asMutable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class GdgListViewModel(state: SavedStateHandle): ViewModel(), AnkoLogger {

    private companion object {
        var cachedLocation : LatLong? = null

        const val MIN_JOB_DURATION = 500L

        const val STATE_KEY_LOCATION = "location"
        const val STATE_KEY_REGION   = "region"
    }

    private val repository by lazy { GdgChapterRepository(GdgApi.retrofitService) }
    private var job: Job? = null

    private
    val location   = state.getLiveData<LatLong>(STATE_KEY_LOCATION, cachedLocation).asImmutable()
    val region     = state.getLiveData<String>(STATE_KEY_REGION).asImmutable()

    val gdgList    = MutableLiveData<List<GdgChapter>>().asImmutable()
    val regionList = MutableLiveData<List<String>>().asImmutable()

    val hasData = gdgList.map { !it.isNullOrEmpty() }.apply { asMutable().value = false }
    val hasRegions = regionList.map { !it.isNullOrEmpty() }.apply { asMutable().value = false }

    val applyEvent = MutableLiveData<Boolean>().asImmutable()
    fun onApply() { applyEvent.asMutable().value = true }
    fun consumeApplyEvent() { applyEvent.asMutable().value = false }

    val status = MutableLiveData<ApiStatus?>().asImmutable()
//    val statusEvent = status.map { it }
//    fun consumeStatusEvent() { statusEvent.asMutable().value = null }

    init {
        info("init, has regions: ${hasRegions.value}, region: ${region.value}, location: ${location.value}")
        performJob()
    }

    private fun performJob() {
        info("performJob(), hasCache: ${GdgChapterRepository.hasCache()}, lastLocation: ${location.value}, currentJob: $job")
        status.asMutable().value = ApiStatus.Loading
        job?.cancel()
        job = viewModelScope.launch {
            this as Job
            info(" - performJob, launch job: $this")
            val r : Result<GdgChapterRepository.Data>
            measureTime {
                r = runCatching {
                    repository.getData(region.value, location.value)
                }
            }.apply {
                info(" - execution time: millis: ${toLongMilliseconds()}, $this")
                if (toLongMilliseconds() < MIN_JOB_DURATION)
                    delay(MIN_JOB_DURATION - toLongMilliseconds())
            }
            info(" - job in the middle, isCancelled: $isCancelled, is same ${job==this}, $this")
            if (job != this || isCancelled) return@launch
            r.onSuccess { data ->
                info(" - performJob, onSuccess, $this")
                gdgList.asMutable().value = data.chapters
                if (data.regions != regionList.value)
                    regionList.asMutable().value = data.regions
                status.asMutable().value = ApiStatus.Success
            }.onFailure { err ->
                info(" - performJob, onFailure, canceled: $isCancelled, $this")
                if (isCancelled) return@onFailure
                error("  - error is: $err"); err.printStackTrace()
                status.asMutable().value = ApiStatus.Error.of(err)
            }
        }.apply {
            invokeOnCompletion { if (job == this) job = null }
        }
    }

    fun retryOnError() {
        info("retryOnError, status: ${status.value}, job: $job")
        performJob()
    }

    fun onLocationUpdated(updatedLocation: Location) {
        val newLocation = LatLong(updatedLocation.latitude, updatedLocation.longitude)
        info("onLocationUpdated, new & old are same: ${newLocation == location.value} \n new lat long $newLocation, full new: ${location.value}")
        if (newLocation != location.value) {
            location.asMutable().value = newLocation
            cachedLocation = newLocation
            performJob()
        }
    }

    fun onRegionChanged(changedRegion: String?) {
        info("onRegionChanged, new & old are same: ${region.value == changedRegion}, new $changedRegion, old ${region.value}")
        if (region.value != changedRegion) {
            region.asMutable().value = changedRegion
            performJob()
        }
    }

}
