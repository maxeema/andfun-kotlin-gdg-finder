package maxeem.america.gdg.ui

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
import maxeem.america.gdg.R
import maxeem.america.gdg.databinding.FragmentHomeBinding
import maxeem.america.gdg.viewmodels.HomeViewModel
import org.jetbrains.anko.dip
import kotlin.math.absoluteValue

class HomeFragment : BaseFragment() {

    private val model by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = model.apply {
            navigateToSearch.observe(viewLifecycleOwner) {
                if (!it) return@observe
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGdgListFragment())
                consumeNavigateToSearch()
            }
        }
        toolbar.setOnMenuItemClickListener { when (it.itemId) {
            R.id.fragmentSearch -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGdgListFragment())
            R.id.fragmentApply -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddGdgFragment())
            R.id.fragmentAbout -> findNavController().navigate(HomeFragmentDirections.actionFragmentHomeToFragmentAbout())
            else -> Unit
        }.let { true }}
        appbar.addOnOffsetChangedListener(BaseOnOffsetChangedListener { _: AppBarLayout, verticalOffset: Int ->
            when (verticalOffset.absoluteValue > app.dip(16)) {
                true -> fab.shrink()
                else -> fab.extend()
            }
        })
    }.root

}
