package com.example.mapdemo.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mapdemo.BuildConfig
import com.example.mapdemo.R
import com.example.mapdemo.data.Location
import com.example.mapdemo.databinding.FragmentMapBinding
import com.example.mapdemo.ui.viewmodel.LocationViewModel
import com.example.mapdemo.ui.viewmodel.ViewModelFactory
import com.example.mapdemo.util.RouteUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels { ViewModelFactory(requireContext()) }
    private var googleMap: GoogleMap? = null
    private val boundsBuilder = LatLngBounds.builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RouteUtils.initialize(requireContext())
        setupMap()
        setupClickListeners()
        observeLocations()

        Toast.makeText(requireContext(), getString(R.string.direction_button),Toast.LENGTH_LONG).show()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupClickListeners() {
        binding.direction.setOnClickListener {
            drawPolyline()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun observeLocations() {
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            locations?.let { updateMapMarkers(it) }
        }
    }

    private fun updateMapMarkers(locations: List<Location>) {
        googleMap?.clear()

        if (locations.isEmpty()) return

        locations.forEach { location ->
            val latLng = LatLng(location.latitude, location.longitude)
//            googleMap?.addMarker(
//                MarkerOptions()
//                    .position(latLng)
//                    .title(location.name)
//                    .snippet(location.address)
//            )
            boundsBuilder.include(latLng)
        }

        // Animate camera to show all markers
        val bounds = boundsBuilder.build()
        val padding = 100 // Padding in pixels
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, padding)
        )
    }

    private fun drawPolyline() {
        googleMap?.clear()
        val locations = viewModel.locations.value ?: return

        if (locations.isNotEmpty()) {

            locations.forEach { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(location.name)
                        .snippet(location.address)
                )
            }


            for (i in 0 until locations.size - 1) {
                val origin = LatLng(locations[i].latitude, locations[i].longitude)
                val destination = LatLng(locations[i + 1].latitude, locations[i + 1].longitude)

                lifecycleScope.launch {
                    RouteUtils.drawRoute(
                        map = googleMap!!,
                        origin = origin,
                        destination = destination,
                        apiKey = BuildConfig.MAPS_API_KEY
                    )
                }
            }
            val bounds = boundsBuilder.build()
            val padding = 100 // Padding in pixels
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, padding)
            )

//            val firstLocation = locations.first()
//            googleMap?.moveCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    LatLng(firstLocation.latitude, firstLocation.longitude),
//                    12f
//                )
//            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 