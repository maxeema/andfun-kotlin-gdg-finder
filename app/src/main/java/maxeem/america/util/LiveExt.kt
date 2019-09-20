package maxeem.america.util

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import maxeem.america.app
import maxeem.america.handler

fun <T> MutableLiveData<T>.asImmutable() = this as LiveData<T>
fun <T> LiveData<T>.asMutable()          = this as MutableLiveData<T>

fun LifecycleOwner.delayed(delay: Long, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED,
                           token: Any? = null, code: ()->Unit) {
    app.handler.postDelayed(delay, token) {
        doOnLifecycle(stateAtLeast, code = code)
    }
}
fun LifecycleOwner.doOnLifecycle(stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, code: ()->Unit) {
    if (lifecycle.currentState.isAtLeast(stateAtLeast))
        code()
}

fun Fragment.delayed(delay: Long, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, token: Any? = null, code: ()->Unit) {
    app.handler.postDelayed(delay, token) {
        if (!(isDetached || isRemoving) && lifecycle.currentState.isAtLeast(stateAtLeast))
            code()
    }
}

fun AppCompatActivity.delayed(delay: Long, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, token: Any? = null, code: ()->Unit) {
    app.handler.postDelayed(delay, token) {
        if (!(isFinishing || isDestroyed) && lifecycle.currentState.isAtLeast(stateAtLeast))
            code()
    }
}

