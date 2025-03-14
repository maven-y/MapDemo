package com.example.mapdemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapdemo.data.Location
import com.example.mapdemo.data.LocationRepository
import com.example.mapdemo.util.LocationUtils
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {
    private val _locations = MutableLiveData<List<Location>?>()
    val locations: MutableLiveData<List<Location>?> get() = _locations

    private val _sortedLocations = MutableLiveData<List<Location>>()
    val sortedLocations: LiveData<List<Location>> = _sortedLocations

    init {
        viewModelScope.launch {
            repository.getAllLocations().collect { locations ->
                _locations.value = locations
                // Reset sorted locations when original list changes
                _sortedLocations.value = emptyList()
            }
        }
    }

    fun getLocationByName(name: String): Location? {
        return _locations.value?.find { it.name == name }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            repository.insertLocation(location)
            // Reset sorted locations when adding new location
            _sortedLocations.value = emptyList()
        }
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            repository.updateLocation(location)
            val currentList = _locations.value?.toMutableList() ?: mutableListOf()
            val index = currentList.indexOfFirst { it.id == location.id }
            if (index != -1) {
                currentList[index] = location
                _locations.postValue(currentList)
                // Reset sorted locations when updating location
                _sortedLocations.value = emptyList()
            }
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            repository.deleteLocation(location)
            // Reset sorted locations when deleting location
            _sortedLocations.value = emptyList()
        }
    }

    fun sortLocationsByDistance(refLat: Double, refLng: Double, ascending: Boolean = true) {
        viewModelScope.launch {
            val locations = _locations.value ?: return@launch
            if (locations.isEmpty()) return@launch

            val locationsWithDistance = locations.map { location ->
                val distance = LocationUtils.calculateDistance(
                    refLat, refLng,
                    location.latitude, location.longitude
                )
                location to distance
            }

            // Sort based on distance
            val sortedLocations = if (ascending) {
                locationsWithDistance.sortedBy { it.second }
            } else {
                locationsWithDistance.sortedByDescending { it.second }
            }.map { it.first }

            _sortedLocations.postValue(sortedLocations)
        }
    }
} 