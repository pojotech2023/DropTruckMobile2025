package com.pojo.droptruck.fragment.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.TripsAdapter
import com.pojo.droptruck.databinding.FragmentLiveTripsBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.tabsadapter.TripsViewPagerAdapter
import com.pojo.droptruck.trips.TripsViewModel
import com.pojo.droptruck.user.act.CustomerMainActivity
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callCustomerViewTripsDetails
import com.pojo.droptruck.utils.callViewTripsDetails
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LiveTripsFragment : BaseFragment(), TripsAdapter.TripsInterface {

    lateinit var mBinding: FragmentLiveTripsBinding

    var userId: String? = ""
    var role: String? = ""
    val mViewModel: TripsViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()

    val enquiryArray = arrayOf(R.drawable.trips_wfd,R.drawable.trips_loading,R.drawable.trips_onroad,
        R.drawable.trips_unloading,R.drawable.trips_pod,R.drawable.trips_complete)


    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentLiveTripsBinding.inflate(inflater,container,false)

        userId = prefs.getValueString(AppConstant.USER_ID)
        role = prefs.getValueString(AppConstant.ROLE_ID).toString()

        if (role.equals(AppConstant.CUSTOMER)) {

            mBinding.tripsTab.visibility = View.GONE
            mBinding.customerLay.visibility = View.VISIBLE
            mBinding.tripsCustomerRecyclerview.visibility = View.VISIBLE
            mBinding.nestedSV.visibility = View.VISIBLE

            initFn() //becz of pagination...

        }else {

            mBinding.tripsTab.visibility = View.VISIBLE
            mBinding.customerLay.visibility = View.GONE
            mBinding.tripsCustomerRecyclerview.visibility = View.GONE
            mBinding.nestedSV.visibility = View.GONE

            val viewPager = mBinding.viewPager
            val tabLayout = mBinding.tabLayout

            viewPager.isUserInputEnabled = false
            viewPager.offscreenPageLimit = 1

            val adapter = TripsViewPagerAdapter(childFragmentManager, lifecycle)
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.icon = ContextCompat.getDrawable(requireActivity(),
                    enquiryArray[position])
            }.attach()

        }

        try {

            mViewModel.wfdListLiveData.observe(viewLifecycleOwner, Observer {

                if (it!=null){
                    dismissProgressDialog()
                    when(it.status){
                        Status.SUCCESS -> {

                            it.data?.currentPage?.let {
                                currentPage = it.toInt()
                            }
                            it.data?.totalPages?.let {
                                totalPage = it
                            }

                            if (it.data?.dataIndents != null &&  it.data.dataIndents.size>0){
                                //enquiryList.clear()
                                enquiryList.addAll(it.data.dataIndents)
                                callAdapter()
                                mBinding.noData.visibility = View.GONE
                                mBinding.btnCreateIndent.visibility = View.GONE
                            }else {
                                clearData()
                            }
                        }
                        Status.ERROR -> {
                            clearData()
                            shortToast(it.message.toString())
                        }
                    }
                }
            })

        }catch (e:Exception) {
            e.printStackTrace()
        }

        mBinding.btnCreateIndent.setOnClickListener {
            (activity as CustomerMainActivity?)
                ?.openPage(R.id.customerCreateIndentFragment,"Create Indent")
        }

        mBinding.nestedSV.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
            { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    currentPage++
                    if (currentPage <= totalPage) {
                        //call API...
                        initFn()
                    }
                }
            })


        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        //initFn()
    }

    private fun initFn() {
        //showProgressDialog()
        userId = prefs.getValueString(AppConstant.USER_ID)
        role = prefs.getValueString(AppConstant.ROLE_ID)

        mViewModel.callConfirmedTrips(userId,role,currentPage)
    }

    private fun clearData() {
        try{
            mBinding.customerLay.visibility = View.VISIBLE
            mBinding.noData.visibility = View.VISIBLE
            mBinding.btnCreateIndent.visibility = View.VISIBLE
            mBinding.tripsCustomerRecyclerview.visibility = View.GONE
            mBinding.nestedSV.visibility = View.GONE
        }catch (e:Exception){
            e.printStackTrace()
        }
        enquiryList.clear()
        callAdapter()
    }

    private fun callAdapter() {
        val tripsAdapter = TripsAdapter(requireActivity(), AppConstant.ALL_TRIPS,enquiryList,this)
        mBinding.tripsCustomerRecyclerview.adapter = tripsAdapter
    }

    override fun callTripsView(id: Int, pos: Int, action: String) {

        try {
            when(action){
                AppConstant.VIEW_ENQUIRY -> {
                    val data = enquiryList[pos]
                    if (role.equals(AppConstant.CUSTOMER)){
                        callCustomerViewTripsDetails(data,data.status.toString(),"",data.id.toString())
                        //callViewTripsDetails(data)
                    }else {
                        callViewTripsDetails(data)
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}