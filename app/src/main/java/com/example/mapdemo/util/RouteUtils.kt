package com.example.mapdemo.util

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.example.mapdemo.api.DirectionsService
import com.example.mapdemo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RouteUtils {
    private lateinit var retrofit: Retrofit
    private lateinit var directionsService: DirectionsService

    fun initialize(context: Context) {
        val baseUrl = context.getString(R.string.maps_api_base_url)
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        directionsService = retrofit.create(DirectionsService::class.java)
    }

    suspend fun drawRoute(map: GoogleMap,
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ) = withContext(Dispatchers.IO) {
        try {
            val originStr = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude},${destination.longitude}"
            
            val response = directionsService.getDirections(
                origin = originStr,
                destination = destinationStr,
                apiKey = apiKey
            )

            response.routes.firstOrNull()?.let { route ->
                val points = PolyUtil.decode(route.overview_polyline.points)
                withContext(Dispatchers.Main) {
                    map.addPolyline(
                        PolylineOptions()
                            .addAll(points)
                            .color(android.graphics.Color.BLUE)
                            .width(5f)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 