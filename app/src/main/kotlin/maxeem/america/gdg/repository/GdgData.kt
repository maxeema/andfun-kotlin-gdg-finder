package maxeem.america.gdg.repository

import maxeem.america.gdg.network.GdgChapter

data class GdgData(val chapters: List<GdgChapter>,
                   val regions : List<String>)