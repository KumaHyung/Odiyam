package com.soapclient.place.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soapclient.place.databinding.ListItemGroupInfoBinding
import com.soapclient.place.domain.entity.GroupInfo

class GroupInfoListAdapter(val onClicked: (groupInfo: GroupInfo, locationClicked: Boolean) -> (Unit)) : ListAdapter<GroupInfo, RecyclerView.ViewHolder>(GroupInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemGroupInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            name.setOnClickListener {
                groupInfo?.let {
                    onClicked(it, false)
                }
            }
            locationContainer.setOnClickListener {
                groupInfo?.let {
                    onClicked(it, true)
                }
            }
        }

        return GroupInfoListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = getItem(position)
        (holder as GroupInfoListItemViewHolder).bind(user, position)
    }

    inner class GroupInfoListItemViewHolder(
        private val binding: ListItemGroupInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroupInfo, position: Int) {
            binding.apply {
                groupInfo = item
                executePendingBindings()
            }
        }
    }
}

private class GroupInfoDiffCallback : DiffUtil.ItemCallback<GroupInfo>() {

    override fun areItemsTheSame(oldItem: GroupInfo, newItem: GroupInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupInfo, newItem: GroupInfo): Boolean {
        return oldItem == newItem
    }
}
