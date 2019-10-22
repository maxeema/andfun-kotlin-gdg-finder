package maxeem.america.gdg.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize data class GdgChapter(
        @Json(name = "chapter_name") val name: String,
        @Json(name = "cityarea") val city: String,
        val country: String,
        val region: String,
        val website: String,
        val geo: LatLong
): Parcelable

@Parcelize data class LatLong(
        val lat: Double,
        val lng: Double
) : Parcelable

data class GdgResponse (
        @Json(name = "filters_") val filters : Map<String, Any>,
        @Json(name = "data")     val chapters: List<GdgChapter>
) {
         val regions = runCatching { filters.getValue("region") as List<String> }.getOrElse { emptyList() }
}
