package maxeem.america.gdg.misc

/**
 * Conf & Const values
 */

object Conf {

    object GDG {

        private const val GDG_BASE_URL  = "https://bit.ly/"
                const val GET_DIRECTORY = "directoryjson"
//        private const val GDG_BASE_URL  = "https://developer.google.com/community/gdg/directory/"
//                const val GET_DIRECTORY = "directory.json"
//        private const val DELAY         = 5_000
//        private const val DELAY_URL   = "http://slowwly.robertomurray.co.uk/delay/$DELAY/url"
//        const val API_BASE_URL  = "$DELAY_URL/$GDG_BASE_URL"
        const val API_BASE_URL  = GDG_BASE_URL

        val REGIONS = arrayOf("Africa", "Asia", "Australia", "Europe", "LATAM", "North America")

    }

}
