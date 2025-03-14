package com.example.mapdemo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY timestamp ASC")
    fun getAllLocations(): Flow<List<Location>>

    @Insert
    suspend fun insertLocation(location: Location)

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("""
        WITH first_location AS (
            SELECT * FROM locations ORDER BY timestamp ASC LIMIT 1
        )
        SELECT l.* FROM locations l
        WHERE l.id != (SELECT id FROM first_location)
        ORDER BY 
            ((l.latitude - (SELECT latitude FROM first_location)) * (l.latitude - (SELECT latitude FROM first_location))) +
            ((l.longitude - (SELECT longitude FROM first_location)) * (l.longitude - (SELECT longitude FROM first_location))) ASC
    """)
    suspend fun getLocationsSortedByDistance(): List<Location>

    @Query("""
        WITH first_location AS (
            SELECT * FROM locations ORDER BY timestamp ASC LIMIT 1
        )
        SELECT l.* FROM locations l
        WHERE l.id != (SELECT id FROM first_location)
        ORDER BY 
            ((l.latitude - (SELECT latitude FROM first_location)) * (l.latitude - (SELECT latitude FROM first_location))) +
            ((l.longitude - (SELECT longitude FROM first_location)) * (l.longitude - (SELECT longitude FROM first_location))) DESC
    """)
    suspend fun getLocationsSortedByDistanceDesc(): List<Location>
} 