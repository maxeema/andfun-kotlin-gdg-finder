package maxeem.america.gdg.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import maxeem.america.util.Consumable
import maxeem.america.util.asImmutable
import maxeem.america.util.asMutable

class HomeViewModel : ViewModel() {

    val navigateToSearch = MutableLiveData<Consumable<Boolean?>>().asImmutable()

    fun onFabClicked() {
        navigateToSearch.asMutable().value = Consumable(true)
    }

}