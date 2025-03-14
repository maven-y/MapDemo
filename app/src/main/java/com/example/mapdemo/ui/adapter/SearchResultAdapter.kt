package com.example.mapdemo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mapdemo.databinding.ItemSearchResultBinding
import com.google.android.libraries.places.api.model.AutocompletePrediction

class SearchResultAdapter(
    private val onItemClick: (AutocompletePrediction) -> Unit
) : ListAdapter<AutocompletePrediction, SearchResultAdapter.SearchResultViewHolder>(
    SearchResultDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(prediction: AutocompletePrediction) {
            binding.apply {
                placeName.text = prediction.getPrimaryText(null)
                placeAddress.text = prediction.getSecondaryText(null)
                root.setOnClickListener {
                    onItemClick(prediction)
                }
            }
        }
    }

    private class SearchResultDiffCallback : DiffUtil.ItemCallback<AutocompletePrediction>() {
        override fun areItemsTheSame(oldItem: AutocompletePrediction, newItem: AutocompletePrediction): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AutocompletePrediction, newItem: AutocompletePrediction): Boolean {
            return oldItem == newItem
        }
    }
} 