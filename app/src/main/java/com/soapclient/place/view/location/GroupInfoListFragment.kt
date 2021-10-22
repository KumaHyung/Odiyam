package com.soapclient.place.view.location

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.soapclient.place.R
import com.soapclient.place.data.Event
import com.soapclient.place.databinding.FragmentGroupInfoBinding
import com.soapclient.place.ext.popBackStackWhenChild
import com.soapclient.place.view.adapter.GroupInfoListAdapter
import com.soapclient.place.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupInfoListFragment : Fragment() {
    private lateinit var binding: FragmentGroupInfoBinding
    private val groupInfoListAdapter: GroupInfoListAdapter by lazy {
        GroupInfoListAdapter { groupInfo, locationClicked ->
            if (locationClicked) {
                moveToGroupLocation(groupInfo.id)
            }
        }
    }

    private val requestStartActivityForUploadImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.let { intent ->
            intent.data?.let { uri ->
                viewModel.uploadImage(uri)
            }
        }
    }

    private val viewModel: LocationViewModel by viewModels({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupInfoBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root
        setMyProfileLocationButtonVisibility(View.GONE)
        observeImageUploadEvent()
        observeMyProfileData()
        initMyProfileImageButton()
        observeGroupList()
        setListAdapter()

        return binding.root
    }

    private fun setListAdapter() {
        binding.groupInfoList.adapter = groupInfoListAdapter
    }

    private fun initMyProfileImageButton() {
        binding.myProfile.imageContainer.setOnClickListener {
            startContentProvider()
        }
    }

    private fun setMyProfileLocationButtonVisibility(visibility: Int) {
        binding.myProfile.locationContainer.visibility = visibility
    }

    private fun moveToGroupLocation(id: String) {
        lifecycleScope.launch {
            viewModel.setSelectedId(id)
            popBackStackWhenChild()
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        requestStartActivityForUploadImage.launch(intent)
    }

    private fun observeMyProfileData() {
        viewModel.myProfile.asLiveData().observe(viewLifecycleOwner) {
            binding.myProfile.groupInfo = it
        }
    }

    private fun observeGroupList() {
        viewModel.grantedUserProfiles.asLiveData().observe(viewLifecycleOwner) { list ->
            groupInfoListAdapter.submitList(list)
        }
    }

    private fun observeImageUploadEvent() {
        viewModel.uploadImageEvent.asLiveData().observe(viewLifecycleOwner) { event ->
            binding.progressBar.isVisible = event is Event.Loading
            when (event) {
                is Event.Loading -> {
                }
                is Event.Success<*> -> {
                    Toast.makeText(requireContext(), getString(R.string.image_upload_success), Toast.LENGTH_SHORT).show()
                }
                is Event.Fail<*> -> {
                    when (event.error) {
                        is String -> {
                        }
                    }
                    Toast.makeText(requireContext(), getString(R.string.image_upload_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
