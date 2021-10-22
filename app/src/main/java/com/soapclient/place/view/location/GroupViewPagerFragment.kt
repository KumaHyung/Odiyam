package com.soapclient.place.view.location

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.soapclient.place.R
import com.soapclient.place.data.Event
import com.soapclient.place.databinding.FragmentGroupViewPagerBinding
import com.soapclient.place.ext.showToastLong
import com.soapclient.place.ext.showToastShort
import com.soapclient.place.ext.showTwoButtonDialog
import com.soapclient.place.view.adapter.*
import com.soapclient.place.viewmodels.LocationViewModel

class GroupViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentGroupViewPagerBinding

    private val viewModel: LocationViewModel by viewModels({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupViewPagerBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val tabLayout = binding.tabs
        val viewPager = binding.viewPager
        viewPager.adapter = GroupViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
        }.attach()

        observeRequestLocationSharingEvent()
        initAddUserButton()

        return binding.root
    }

    private fun initAddUserButton() {
        binding.addUser.setOnClickListener {
            showSharingRequestDialog()
        }
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            GROUP_INFO_LIST_PAGE_INDEX -> R.drawable.btn_group
            GROUP_GRANT_WAIT_PAGE_INDEX -> R.drawable.btn_grant_user
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            GROUP_INFO_LIST_PAGE_INDEX -> getString(R.string.group_info)
            GROUP_GRANT_WAIT_PAGE_INDEX -> getString(R.string.wait_grant)
            else -> null
        }
    }

    private fun showSharingRequestDialog() {
        showTwoButtonDialog(getString(R.string.guidance),
            getString(R.string.request_share_location_description),
            R.layout.item_alert_edit_text,
            getString(R.string.cancel),
            getString(R.string.share_request),
            {
                it.dismiss()
            },
            {
                with(it.findViewById<TextInputEditText>(R.id.email_address).text.toString()) {
                    if (isNotEmpty()) {
                        viewModel.requestLocationSharingByEmail(trim())
                    } else {
                        // TODO: editText 비어있는 상황 처리
                    }
                }
                it.dismiss()
            })
    }

    private fun observeRequestLocationSharingEvent() {
        viewModel.requestLocationSharingEvent.asLiveData().observe(viewLifecycleOwner) { event ->
            when (event) {
                is Event.Loading -> {

                }

                is Event.Success<*> -> {
                    when (event.data) {
                        is Int -> {
                            showToastShort(getString(event.data))
                        }
                    }
                }

                is Event.Fail<*> -> {
                    when (event.error) {
                        is Int -> {
                            showToastLong(getString(event.error))
                        }
                    }
                }
            }
        }
    }
}
