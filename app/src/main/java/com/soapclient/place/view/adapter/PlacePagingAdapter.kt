package com.soapclient.place.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soapclient.place.databinding.ListItemPlaceBinding
import com.soapclient.place.domain.entity.Place

class PlacePagingAdapter(val clickListener: (Place) -> Unit) : PagingDataAdapter<Place, PlacePagingAdapter.LocationViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ListItemPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return LocationViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
                getItem(position)?.let {
                    clickListener(it)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        location?.let {
            holder.bind(it)
        }
    }

    class LocationViewHolder(
            private val binding: ListItemPlaceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Place) {
            binding.apply {
                place = item
                executePendingBindings()
            }
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}
