package com.soapclient.place.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soapclient.place.databinding.ListItemPlaceHistoryBinding
import com.soapclient.place.domain.entity.Place

class PlaceHistoryAdapter(val onClicked: (place: Place, delete: Boolean) -> (Unit)) : ListAdapter<Place, RecyclerView.ViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemPlaceHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            name.setOnClickListener {
                place?.let {
                    onClicked(it, false)
                }
            }
            delete.setOnClickListener {
                place?.let {
                    onClicked(it, true)
                }
            }
        }
        return LocationHistoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val history = getItem(position)
        (holder as LocationHistoryItemViewHolder).bind(history, position)
    }

    inner class LocationHistoryItemViewHolder(
        private val binding: ListItemPlaceHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Place, position: Int) {
            binding.apply {
                place = item
                executePendingBindings()
            }
        }
    }
}

private class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {

    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}
