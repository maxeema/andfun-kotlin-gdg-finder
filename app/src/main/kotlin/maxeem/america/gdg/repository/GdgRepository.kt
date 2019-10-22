package maxeem.america.gdg.repository

import maxeem.america.gdg.network.LatLong
import org.jetbrains.anko.AnkoLogger

interface GdgRepository : AnkoLogger {

    suspend fun getData(region: String?, location: LatLong?) : GdgData

}
