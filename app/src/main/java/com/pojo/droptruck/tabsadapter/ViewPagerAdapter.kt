package com.pojo.droptruck.tabsadapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pojo.droptruck.enquiry.CancelledEnquiryFragment
import com.pojo.droptruck.enquiry.ConfirmedFragment
import com.pojo.droptruck.enquiry.FollowUpFragment
import com.pojo.droptruck.enquiry.QuotedFragment
import com.pojo.droptruck.enquiry.UnQuotedFragment
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant


private const val NUM_TABS = 5

public class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SALES)){
            return 5
        }else {
            return 3
        }
        //return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return UnQuotedFragment()
            1 -> return QuotedFragment()
            2 -> return ConfirmedFragment()
            3 -> return CancelledEnquiryFragment()
            4 -> return FollowUpFragment()
        }
        return FollowUpFragment()
    }
}