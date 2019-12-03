package maxeem.america.util

class Consumable<T>(private var data: T?) {

    fun consume() = data?.also { data = null }

}