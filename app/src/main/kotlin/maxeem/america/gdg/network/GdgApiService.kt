package maxeem.america.gdg.network

import kotlinx.coroutines.Deferred
import maxeem.america.gdg.misc.Conf
import retrofit2.http.GET

interface GdgApiService {

    @GET(Conf.GDG.GET_DIRECTORY)
    fun getChaptersAsync(): Deferred<GdgResponse>

}
