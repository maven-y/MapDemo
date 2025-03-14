package com.example.mapdemo.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapdemo.BuildConfig
import com.example.mapdemo.R
import com.example.mapdemo.data.Location
import com.example.mapdemo.databinding.DialogLocationSearchBinding
import com.example.mapdemo.ui.viewmodel.LocationViewModel
import com.example.mapdemo.ui.adapter.SearchResultAdapter
import com.example.mapdemo.ui.viewmodel.ViewModelFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class LocationSearchDialog(
    private val locationName: String,
    private val initialQuery: String? = null,
    private val locationToEdit: Location? = null
) : DialogFragment() {
    private var _binding: DialogLocationSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var adapter: SearchResultAdapter
    private lateinit var placesClient: PlacesClient
    private val isUpdate: Boolean = locationToEdit != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLocationSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlacesClient()
        setupRecyclerView()
        setupSearchInput()
        
        // Set initial query if provided
        val queryToUse = initialQuery ?: locationName
        if (queryToUse.isNotEmpty()) {
            binding.searchInput.setText(queryToUse)
            searchLocations(queryToUse)
        }
    }

    private fun setupPlacesClient() {
        Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(requireContext())
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter { prediction ->
            fetchPlaceDetails(prediction)
        }
        binding.searchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@LocationSearchDialog.adapter
        }
    }

    private fun setupSearchInput() {
        binding.searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s.isNullOrBlank()) {
                    adapter.submitList(emptyList())
                } else {
                    searchLocations(s.toString())
                }
            }
        })
    }

    private fun searchLocations(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                adapter.submitList(response.autocompletePredictions)
            }
            .addOnFailureListener { exception ->
                Log.e("Places", "Error fetching predictions: ${exception.message}")
            }
    }

    private fun fetchPlaceDetails(prediction: AutocompletePrediction) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        )

        val request = FetchPlaceRequest.builder(prediction.placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val newLocation = Location(
                    id = if (isUpdate) locationToEdit?.id ?: 0 else 0,
                    name = place.name ?: "",
                    latitude = place.latLng?.latitude ?: 0.0,
                    longitude = place.latLng?.longitude ?: 0.0,
                    address = place.address ?: ""
                )
                
                if (isUpdate) {
                    viewModel.updateLocation(newLocation)
                } else {
                    viewModel.addLocation(newLocation)
                }
                dismiss()
            }
            .addOnFailureListener { exception ->
                Log.e("Places", "Error fetching place: ${exception.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 