package maxeem.america.util

import maxeem.america.app

/**
 * String extensions
 */

fun Int.asString() = app.getString(this)
fun Int.asString(vararg args: Any) = app.getString(this, *args)

fun String.fromHtml() = Utils.fromHtml(this)

private val PUNCTUATION = listOf(", ", "; ", ": ", " ")
/**
 * Truncate long text with a preference for word boundaries and without trailing punctuation.
 */
fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)) {
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}