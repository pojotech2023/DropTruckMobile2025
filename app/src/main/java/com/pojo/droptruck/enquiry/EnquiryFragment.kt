package com.pojo.droptruck.enquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.FragmentEnquiryBinding
import com.pojo.droptruck.tabsadapter.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnquiryFragment : Fragment() {

    lateinit var mBinding: FragmentEnquiryBinding

    /*val enquiryArray = arrayOf(
        "Quoted",
        "UnQuoted",
        "Confirmed"
    )*/

    val enquiryArray = arrayOf(R.drawable.unquoted,R.drawable.quoted,R.drawable.confirmed,
        R.drawable.cancelled,R.drawable.baseline_trending_up_24)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEnquiryBinding.inflate(inflater,container,false)

        val viewPager = mBinding.viewPager
        val tabLayout = mBinding.tabLayout

        viewPager.isUserInputEnabled = false
        //viewPager.offscreenPageLimit = 1

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            //tab.text = enquiryArray[position]
            //tab.icon = resources.getDrawable(enquiryArray[position])
            tab.icon = ContextCompat.getDrawable(requireActivity(),
                enquiryArray[position])
        }.attach()


        return mBinding.root
    }

}