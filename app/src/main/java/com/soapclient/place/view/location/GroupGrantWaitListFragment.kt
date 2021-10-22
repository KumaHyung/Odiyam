package com.soapclient.place.view.location

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.soapclient.place.databinding.FragmentGroupGrantWaitBinding
import com.soapclient.place.view.adapter.GroupGrantWaitListAdapter
import com.soapclient.place.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupGrantWaitListFragment : Fragment() {
    private lateinit var binding: FragmentGroupGrantWaitBinding
    private val groupGrantWaitListAdapter: GroupGrantWaitListAdapter by lazy {
        GroupGrantWaitListAdapter { groupInfo, granted ->
            if (granted) {
                viewModel.grantLocationSharingRequest(groupInfo.id)
            } else {
                viewModel.denyLocationSharingRequest(groupInfo.id)
            }
        }
    }
    private val viewModel: LocationViewModel by viewModels({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupGrantWaitBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root
        observeGroupWaitList()
        setListAdapter()

        return binding.root
    }

    private fun setListAdapter() {
        binding.groupGrantWaitList.adapter = groupGrantWaitListAdapter
    }

    private fun observeGroupWaitList() {
        viewModel.waitUserProfiles.asLiveData().observe(viewLifecycleOwner) { list ->
            groupGrantWaitListAdapter.submitList(list)
        }
    }
}
