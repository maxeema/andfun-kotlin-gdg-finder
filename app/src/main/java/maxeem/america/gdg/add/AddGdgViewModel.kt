package maxeem.america.gdg.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import maxeem.america.util.asImmutable
import maxeem.america.util.asMutable

class AddGdgViewModel : ViewModel() {

    val showSnackbarEvent = MutableLiveData<Boolean>().asImmutable()

    fun onSubmitApplication() {
        showSnackbarEvent.asMutable().value = true
    }

    fun consumeSnackbarEvent() {
        showSnackbarEvent.asMutable().value = null
    }

}
