package maxeem.america.gdg.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import maxeem.america.app
import maxeem.america.base.BaseFragment
import maxeem.america.gdg.R
import maxeem.america.gdg.databinding.FragmentGdgListBinding
import maxeem.america.gdg.network.GdgChapter
import maxeem.america.gdg.viewmodels.GdgListViewModel
import maxeem.america.util.asString
import maxeem.america.util.delayed
import maxeem.america.util.visibleOn
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import kotlin.time.ExperimentalTime

private const val LOCATION_PERMISSION         = "android.permission.ACCESS_COARSE_LOCATION"
private const val LOCATION_PERMISSION_REQUEST = 1

@ExperimentalTime
class GdgListFragment : BaseFragment() {

    private val model: GdgListViewModel by viewModels { SavedStateViewModelFactory(app, this)}
    private val locationHelper by lazy { LocationHelper(requireContext()) {
        model.onLocationUpdated(it)
    }}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
        = FragmentGdgListBinding.inflate(inflater).apply {

        lifecycleOwner = viewOwner
        viewModel = model

        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val adapter = GdgListAdapter { v->
            startActivity(Intent(Intent.ACTION_VIEW, (v.tag as GdgChapter).website.toUri()))
        }.apply {
            setFilterable(viewLifecycleOwner.lifecycleScope) {
                empty.visibleOn(it.isEmpty())
            }
            recycler.adapter = this
        }
        recycler.addItemDecoration(AdapterItemsDashDecoration(R.color.listItemsDecorationLine))
        val searchItem = toolbar.menu.findItem(R.id.search).apply {
            isVisible = false
        }
        val searchView = (searchItem.actionView as SearchView).apply {
            queryHint = R.string.where_search.asString(R.string.global.asString())
            isSubmitButtonEnabled = false
            setOnCloseListener {
                adapter.filter("")
                appbar.setExpanded(true, true)
                false
            }
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                var lastQueryText = ""
                override fun onQueryTextChange(newText: String?) = true.apply exit@ {
                    val newQueryText = newText?.trim() ?: ""
                    if (lastQueryText != newQueryText) {
                        adapter.filter(newQueryText)
                        lastQueryText = newQueryText
                    }
                    appbar.setExpanded(true, true)
                }
                override fun onQueryTextSubmit(query: String?) = false
            })
        }
        model.gdgList.observe(viewLifecycleOwner) { gdgList ->
            adapter.submitList(gdgList) {
                info(" model gdgList observe, submit data, size: ${gdgList.size}")
                appbar.setExpanded(true)
                appbar.setLifted(false)
                val regionLabel = model.region.value ?: R.string.global.asString()
                toolbar.title = if (model.region.value == null) R.string.gdg_global.asString() else R.string.gdg_in.asString(regionLabel)
                searchView.queryHint = R.string.where_search.asString(regionLabel)
                searchItem.isVisible = gdgList.isNotEmpty()
                searchView.onActionViewCollapsed()
                recycler.scrollToPosition(0)
                bottomAppbar.performShow()
            }
        }
        model.applyEvent.observe(viewLifecycleOwner) {
            if (!it) return@observe
            findNavController().navigate(GdgListFragmentDirections.actionFragmentSearchToFragmentApply())
            model.consumeApplyEvent()
        }
        model.regionList.observe(viewLifecycleOwner) { regionList ->
            if (regionList.isNullOrEmpty()) return@observe
            info(" regionList observe, data size: ${regionList.size}, model region: ${model.region.value}")
            regions.removeAllViews()
            val chipInflater = LayoutInflater.from(regions.context)
            regionList.map { regionName ->
                chipInflater.inflate(R.layout.region_chip, regions, false).apply { this as Chip
                    tag = regionName; text = regionName
                    regions.addView(this)
                    if (model.region.value == regionName)
                        isChecked = true
                }
            }
        }
        regions.setOnCheckedChangeListener { group, id ->
            val newRegion = if (id == View.NO_ID) null else group.findViewById<Chip>(id).tag as String
            info(" chip checked: $id, new region: $newRegion, model region: ${model.region.value}")
            if (model.region.value != newRegion)
                model.onRegionChanged(newRegion)
        }

    }.root

    override fun onStart() { super.onStart()
        if (locationHelper.client != null) {
            locationHelper.listenToUpdates()
        } else delayed(2_000, stateAtLeast = Lifecycle.State.STARTED) {
            if (!locationHelper.hasRequestedLastLocationOnStart) {
                locationHelper.hasRequestedLastLocationOnStart = true
                if (checkLocationPermission())
                    locationHelper.requestLastLocation()
            }
        }
    }
    override fun onStop() { super.onStop()
        locationHelper.stopUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                info("on Location Permission response, has granted: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper.requestLastLocation()
                }
            }
        }
    }

    private fun checkLocationPermission() : Boolean {
        if (!locationHelper.isPermissionGranted()) {
            info("requesting for Location Permission...")
            requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQUEST)
            return false
        }
        info("Location Permission granted")
        return true
    }

    //
    private class LocationHelper(val ctx: Context, val onLocationChanged: (location: Location)->Unit): AnkoLogger {

        var callback : LocationCallback? = null
            private set
        var client: FusedLocationProviderClient? = null
            private set
        var listeningToUpdates : Boolean = false
            private set
        var hasRequestedLastLocationOnStart : Boolean  = false

        fun requestLastLocation() {
            info("call requestLastLocation")
            val activeClient = client ?: LocationServices.getFusedLocationProviderClient(ctx)
            if (client == null) {
                client = activeClient
                info(" requestLastLocation, new client: $client")
                activeClient.lastLocation.apply {
                    addOnSuccessListener { location ->
                        info(" requestLastLocation, success location: $location")
                        if (location == null) {
                            startUpdates()
                        } else {
                            onGotLocation(location)
                        }
                    }
                    addOnFailureListener {
                        error(" requestLastLocationfailure location, $it", it)
                    }
                }
            }
        }

        fun startUpdates() {
            if (callback == null) {
                callback = object: LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        info(" startUpdates, onLocationResult: $locationResult")
                        val location = locationResult?.lastLocation ?: return
                        onGotLocation(location)
                    }
                }
                info("call startUpdates, new callback: $callback")
            }
            listenToUpdates()
        }

        fun listenToUpdates() {
            info("call listenToUpdates\n client: $client \n callback: $callback")
            if (!listeningToUpdates && client != null && callback != null) {
                client!!.requestLocationUpdates(
                    LocationRequest().setPriority(LocationRequest.PRIORITY_LOW_POWER),
                            callback, Looper.getMainLooper()
                )
                listeningToUpdates = true
            }
        }
        fun stopUpdates() {
            info("call stopUpdates: client $client, callback $callback")
            if (listeningToUpdates && client != null && callback != null) {
                client?.removeLocationUpdates(callback)
                listeningToUpdates = false
            }
        }

        fun onGotLocation(location: Location) {
            onLocationChanged(location)
            release()
        }
        fun release() {
            stopUpdates()
            client = null
            callback = null
        }

        fun isPermissionGranted() : Boolean
             = ContextCompat.checkSelfPermission(ctx, LOCATION_PERMISSION ) == PackageManager.PERMISSION_GRANTED

    }

}
