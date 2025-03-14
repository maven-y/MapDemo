package com.example.mapdemo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mapdemo.R
import com.example.mapdemo.data.Location
import com.example.mapdemo.databinding.ItemLocationBinding
import com.example.mapdemo.util.LocationUtils
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.card.MaterialCardView

class LocationAdapter(private val onDeleteClick: (Location) -> Unit, private val onEditClick: (Location) -> Unit) : ListAdapter<Location, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
        
        // Show primary label for first item
        holder.binding.primaryLabel.visibility = if (position == 0) View.VISIBLE else View.GONE
        
        // Calculate and show distance from first location
        if (position > 0) {
            val firstLocation = getItem(0)
            val distance = LocationUtils.calculateDistance(
                firstLocation.latitude, firstLocation.longitude,
                location.latitude, location.longitude
            )
            holder.binding.distanceLabel.apply {
                text = String.format("Distance: %.0f kms", distance)
                visibility = View.VISIBLE
            }
        } else {
            holder.binding.distanceLabel.visibility = View.GONE
        }
    }

    inner class LocationViewHolder(
        val binding: ItemLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location) {
            binding.apply {
                tvLocationName.text = location.name
                tvLocationAddress.text = location.address
                btnDelete.setOnClickListener { onDeleteClick(location) }
                btnEdit.setOnClickListener { onEditClick(location) }
            }
        }
    }

    private class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.name == newItem.name && 
                   oldItem.address == newItem.address &&
                   oldItem.latitude == newItem.latitude &&
                   oldItem.longitude == newItem.longitude
        }

        override fun getChangePayload(oldItem: Location, newItem: Location): Any? {
            return if (oldItem.name != newItem.name || 
                      oldItem.address != newItem.address ||
                      oldItem.latitude != newItem.latitude ||
                      oldItem.longitude != newItem.longitude) {
                true
            } else {
                null
            }
        }
    }
} 