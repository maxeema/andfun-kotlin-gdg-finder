package maxeem.america.gdg.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import maxeem.america.util.asImmutable
import maxeem.america.util.asMutable

class HomeViewModel : ViewModel() {

    val navigateToSearch = MutableLiveData<Boolean>().asImmutable()

    fun onFabClicked() {
        navigateToSearch.asMutable().value = true
    }

    fun consumeNavigateToSearch() {
        navigateToSearch.asMutable().value = false
    }

}