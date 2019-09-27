package maxeem.america.gdg.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import maxeem.america.gdg.misc.Conf
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface GdgApiService {

    @GET(Conf.GDG.GET_DIRECTORY)
    fun getChaptersAsync(): Deferred<GdgResponse>

}

private val retrofit = Retrofit.Builder().apply {
    baseUrl(Conf.GDG.API_BASE_URL)
    addCallAdapterFactory(CoroutineCallAdapterFactory())
    addConverterFactory(
        MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build())
    )
}.build()

object GdgApi {

    val retrofitService : GdgApiService by lazy { retrofit.create(GdgApiService::class.java) }

}
