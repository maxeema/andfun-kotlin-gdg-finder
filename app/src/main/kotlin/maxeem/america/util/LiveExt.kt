package maxeem.america.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.asImmutable() = this as LiveData<T>
fun <T> LiveData<T>.asMutable()          = this as MutableLiveData<T>
