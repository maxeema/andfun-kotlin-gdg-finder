package maxeem.america.gdg.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import maxeem.america.base.BaseFragment
import maxeem.america.gdg.R
import maxeem.america.gdg.databinding.AddGdgFragmentBinding
import org.jetbrains.anko.design.snackbar

class AddGdgFragment : BaseFragment() {

    private val model : AddGdgViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
    = AddGdgFragmentBinding.inflate(inflater).apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = model.apply {
            showSnackbarEvent.observe(viewLifecycleOwner) {
                if (!it) return@observe
                view?.snackbar(R.string.application_submitted)
                consumeSnackbarEvent()
            }
        }
    }.root

}
