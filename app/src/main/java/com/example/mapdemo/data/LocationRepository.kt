package com.example.mapdemo.data

import kotlinx.coroutines.flow.Flow

class LocationRepository(private val locationDao: LocationDao) {

    fun getAllLocations(): Flow<List<Location>> = locationDao.getAllLocations()

    suspend fun insertLocation(location: Location) = locationDao.insertLocation(location)

    suspend fun updateLocation(location: Location) = locationDao.updateLocation(location)

    suspend fun deleteLocation(location: Location) = locationDao.deleteLocation(location)

    suspend fun getLocationsSortedByDistance(): List<Location> =
        locationDao.getLocationsSortedByDistance()

    suspend fun getLocationsSortedByDistanceDesc(): List<Location> =
        locationDao.getLocationsSortedByDistanceDesc()
} 