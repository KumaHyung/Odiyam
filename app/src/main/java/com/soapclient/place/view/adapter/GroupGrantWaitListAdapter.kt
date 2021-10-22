package com.soapclient.place.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soapclient.place.databinding.ListItemGroupGrantBinding
import com.soapclient.place.domain.entity.GroupInfo

class GroupGrantWaitListAdapter(val onClicked: (groupInfo: GroupInfo, granted: Boolean) -> (Unit)) : ListAdapter<GroupInfo, RecyclerView.ViewHolder>(GroupJoinGrantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemGroupGrantBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            reject.setOnClickListener {
                groupInfo?.let {
                    onClicked(it, false)
                }
            }
            grant.setOnClickListener {
                groupInfo?.let {
                    onClicked(it, true)
                }
            }
        }

        return GroupJoinGrantListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val profile = getItem(position)
        (holder as GroupJoinGrantListItemViewHolder).bind(profile, position)
    }

    inner class GroupJoinGrantListItemViewHolder(
        private val binding: ListItemGroupGrantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroupInfo, position: Int) {
            binding.apply {
                groupInfo = item
                executePendingBindings()
            }
        }
    }
}

private class GroupJoinGrantDiffCallback : DiffUtil.ItemCallback<GroupInfo>() {

    override fun areItemsTheSame(oldItem: GroupInfo, newItem: GroupInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupInfo, newItem: GroupInfo): Boolean {
        return oldItem == newItem
    }
}
