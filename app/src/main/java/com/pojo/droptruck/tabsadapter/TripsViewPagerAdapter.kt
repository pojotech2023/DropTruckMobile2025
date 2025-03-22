package com.pojo.droptruck.tabsadapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pojo.droptruck.trips.CompletedTripsFragment
import com.pojo.droptruck.trips.OntheRoadFragment
import com.pojo.droptruck.trips.TripsLoadingFragment
import com.pojo.droptruck.trips.TripsPODragment
import com.pojo.droptruck.trips.TripsUnLoadingFragment
import com.pojo.droptruck.trips.WaitingForDriverFragment


private const val NUM_TABS = 6

public class TripsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return WaitingForDriverFragment()
            1 -> return TripsLoadingFragment()
            2 -> return OntheRoadFragment()
            3 -> return TripsUnLoadingFragment()
            4 -> return TripsPODragment()
            5 -> return CompletedTripsFragment()
        }
        return CompletedTripsFragment()
    }
}