package com.pojo.droptruck.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pojo.droptruck.databinding.FragmentDashboardBinding
import com.pojo.droptruck.prefs
import com.pojo.droptruck.tabsadapter.CustomerHomeViewPagerAdapter
import com.pojo.droptruck.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    lateinit var mBinding: FragmentDashboardBinding

    val enquiryArray = arrayOf("UnQuoted","Quoted")
    //val enquiryArray = arrayOf(R.drawable.unquoted, R.drawable.quoted)

    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout

    val mViewModel: DashboardViewModel by viewModels()
    var userId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =  FragmentDashboardBinding.inflate(inflater, container, false)

        userId = prefs.getValueString(AppConstant.USER_ID)

        mBinding.btnCreateIndent.setOnClickListener {
            callCreateIndent()
        }

        mBinding.lay.btnLayout.setOnClickListener {
            //callBookTrip()
            callCreateIndent()
        }

        try {

            val userType = prefs.getValueString(AppConstant.ROLE_ID)

            if (userType.equals(AppConstant.SUPPLIER)) {
                mBinding.lay.btnLayout.visibility = View.GONE
                mBinding.btnCreateIndent.visibility = View.GONE
            }

            if (userType.equals(AppConstant.CUSTOMER)) {
                mBinding.lay.mainLay.visibility = View.GONE
                mBinding.btnCreateIndent.visibility = View.GONE
                mBinding.customerLay.visibility = View.VISIBLE
            }

            if (!userType.equals(AppConstant.CUSTOMER)) {
                mBinding.countLay.visibility = View.VISIBLE
                mViewModel.getIndentsCount(userId)
            }


        }catch (e:Exception){
            e.printStackTrace()
        }

        viewPager = mBinding.viewPager
        tabLayout = mBinding.tabLayout

        val adapter = CustomerHomeViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        setTabs(0,0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                getCount()
            }

        })

        try {
            mViewModel.resultLiveData.observe(viewLifecycleOwner,Observer{
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data?.data!=null) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                setTabs(it.data.data?.unquotedIndents!!,it.data.data?.quotedIndents!!)
                            }
                        }
                    }
                    Status.ERROR -> {

                    }
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {

            mViewModel.countLiveData.observe(viewLifecycleOwner,Observer{
                when(it.status){
                    Status.SUCCESS -> {

                        it.data?.data?.let {

                            lifecycleScope.launch(Dispatchers.Main) {

                                mBinding.valQuoted.setText(it.quotedCount.toString())
                                mBinding.valUnquoted.setText(it.unquotedCount.toString())
                                mBinding.valConfirmed.setText(it.confirmedCount.toString())
                                mBinding.valCancelled.setText(it.CancelledCount.toString())
                                mBinding.valCompleted.setText(it.completedCount.toString())
                                mBinding.valWfd.setText(it.waitingForDriverCount.toString())
                                mBinding.valLoad.setText(it.loadingCount.toString())
                                mBinding.valUnload.setText(it.unloadingCount.toString())
                                mBinding.valOtr.setText(it.onTheRoadCount.toString())
                                mBinding.valPod.setText(it.podCount.toString())


                            }

                        }

                    }
                    Status.ERROR -> {

                    }
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
        }

        return mBinding.root
    }

    fun getCount() {
        mViewModel.getEnqCount(userId)
    }

    private fun setTabs(unQuote: Int, quote: Int) {
        try {

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                setTabName(tab,position,unQuote,quote)
            }.attach()

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun setTabName(tab: TabLayout.Tab, position: Int,unQuote: Int, quote: Int) {
        try {
            when (position) {
                0 -> {
                    tab.text = enquiryArray[position] + " ("+unQuote+")"
                }

                1 -> {
                    tab.text = enquiryArray[position] + " ("+quote+")"
                }
                else -> {
                    tab.text = ""
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}