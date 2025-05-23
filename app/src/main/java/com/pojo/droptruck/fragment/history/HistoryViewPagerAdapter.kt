package com.pojo.droptruck.fragment.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pojo.droptruck.enquiry.QuotedFragment
import com.pojo.droptruck.enquiry.UnQuotedFragment


private const val NUM_TABS = 2
public class HistoryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return UnQuotedFragment()
            1 -> return QuotedFragment()
        }
        return QuotedFragment()
    }
}

