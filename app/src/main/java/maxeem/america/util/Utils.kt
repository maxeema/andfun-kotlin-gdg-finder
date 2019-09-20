package maxeem.america.util

import android.text.format.DateUtils.*
import androidx.core.text.HtmlCompat
import maxeem.america.app

object Utils {

    fun fromHtml(s: String) = HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_COMPACT)

    fun formatError(msgId: Int, err: Throwable) = formatError(msgId.asString(), err)
    fun formatError(msg: String, err: Throwable) =
        msg.plus("<br/><br/><small>[ ${err.javaClass.simpleName} ]").let {
            if (err.message.isNullOrBlank()) it else it.plus("<br/>${err.message}")
        }.plus("</small>").fromHtml()

    @JvmStatic
    fun formatDateTime(millis: Long) = formatDateTime(app, millis, FORMAT_SHOW_DATE or FORMAT_SHOW_TIME or FORMAT_ABBREV_ALL)

}
