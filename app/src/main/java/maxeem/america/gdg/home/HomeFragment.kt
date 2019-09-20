package maxeem.america.gdg.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.BaseOnOffsetChangedListener
import maxeem.america.app
import maxeem.america.base.BaseFragment
import maxeem.america.gdg.databinding.HomeFragmentBinding
import org.jetbrains.anko.dip
import kotlin.math.absoluteValue

class HomeFragment : BaseFragment() {

    private val model by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = HomeFragmentBinding.inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = model.apply {
            navigateToSearch.observe(viewLifecycleOwner) {
                if (!it) return@observe
                consumeNavigateToSearch()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGdgListFragment())
            }
        }
        appbar.addOnOffsetChangedListener(BaseOnOffsetChangedListener { _: AppBarLayout, verticalOffset: Int ->
            when (verticalOffset.absoluteValue > app.dip(16)) {
                true -> fab.shrink()
                else -> fab.extend()
            }
        })
    }.root

}
