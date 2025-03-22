package com.pojo.droptruck.trips

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.TripsAdapter
import com.pojo.droptruck.databinding.FragmentWaitingForDriverBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callCreatePOD
import com.pojo.droptruck.utils.callViewTripsDetails
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsPODragment : BaseFragment(), TripsAdapter.TripsInterface {
    lateinit var mainBinding: FragmentWaitingForDriverBinding
    val mViewModel: TripsViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "QuotedFragment"
    var userId: String? = ""

    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mainBinding = FragmentWaitingForDriverBinding.inflate(inflater, container, false)

        mViewModel.podListLiveData.observe(viewLifecycleOwner, Observer {

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
                            if (currentPage == 1){
                                enquiryList.clear()
                            }

                            enquiryList.addAll(it.data.dataIndents)
                            callAdapter()
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

        mainBinding.nestedSV.setOnScrollChangeListener(
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

        initFn() //becz of pagination...

        return mainBinding.root
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //initFn()
    }

    private fun initFn() {
        showProgressDialog()
        userId = prefs.getValueString(AppConstant.USER_ID)
        val role = prefs.getValueString(AppConstant.ROLE_ID)

        mViewModel.callPODTripsList(userId,role, currentPage)
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter()
    }

    private fun callAdapter() {
        val tripsAdapter = TripsAdapter(requireActivity(), AppConstant.POD,enquiryList,this)
        mainBinding.tripsRecyclerview.adapter = tripsAdapter
    }

    override fun callTripsView(id: Int, pos: Int, action: String) {
        when(action){
            AppConstant.CREATE_POD -> {
                val data = enquiryList[pos]
                callCreatePOD(id)
            }
            AppConstant.VIEW_ENQUIRY -> {
                val data = enquiryList[pos]
                callViewTripsDetails(data,AppConstant.TRIPS_ONTHEROAD)
            }
        }
    }


}