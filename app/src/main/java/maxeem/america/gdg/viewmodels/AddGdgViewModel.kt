package maxeem.america.gdg.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import maxeem.america.util.asImmutable
import maxeem.america.util.asMutable

class AddGdgViewModel : ViewModel() {

    val submitEvent = MutableLiveData<Boolean?>().asImmutable()

    fun onSubmitEvent() {
        submitEvent.asMutable().value = true
    }

    fun consumeSubmitEvent() {
        submitEvent.asMutable().value = null
    }

}
