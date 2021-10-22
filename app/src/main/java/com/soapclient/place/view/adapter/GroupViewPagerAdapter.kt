package com.soapclient.place.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.soapclient.place.view.location.GroupGrantWaitListFragment
import com.soapclient.place.view.location.GroupInfoListFragment

const val GROUP_INFO_LIST_PAGE_INDEX = 0
const val GROUP_GRANT_WAIT_PAGE_INDEX = 1

class GroupViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
            GROUP_INFO_LIST_PAGE_INDEX to { GroupInfoListFragment() },
            GROUP_GRANT_WAIT_PAGE_INDEX to { GroupGrantWaitListFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}