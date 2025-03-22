package com.pojo.droptruck.enquiry

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pojo.droptruck.adapter.EnquiryAdapter
import com.pojo.droptruck.databinding.FragmentCancelledEnquiryBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callIndentEdit
import com.pojo.droptruck.utils.callViewEnquiry
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelledEnquiryFragment : BaseFragment(),EnquiryAdapter.EnquiryInterface {

    lateinit var mainBinding: FragmentCancelledEnquiryBinding
    val mViewModel: EnquiryViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "CancelledEnquiryFragment"
    var userId: String? = ""

    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainBinding = FragmentCancelledEnquiryBinding.inflate(inflater, container, false)

        mViewModel.cancelIndentListLiveData.observe(viewLifecycleOwner, Observer {

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
                            //enquiryList.clear() //becz of pagination...
                            enquiryList.addAll(it.data.dataIndents) //becz of pagination...
                            callAdapter(AppConstant.CANCELLED)
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

        mViewModel.restoreLiveData.observe(viewLifecycleOwner,Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                initFn()
                            }else{
                                //shortToast("No Response")
                            }
                        }
                        Status.ERROR -> {
                            shortToast(it.message!!)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        })

        try {
            mainBinding.nestedSV.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener
                { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                        currentPage++
                        if (currentPage <= totalPage) {
                            //call API...
                            initFn()
                        }
                    }
                })
        }catch (e:Exception){
            e.printStackTrace()
        }

        initFn() //becz of pagination...

        return mainBinding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //initFn() //becz of pagination...
    }

    private fun initFn() {
        showProgressDialog()
        userId = prefs.getValueString(AppConstant.USER_ID)
        val role = prefs.getValueString(AppConstant.ROLE_ID)

        when(role) {
            AppConstant.SALES -> {
                mViewModel.getCancelledList(role,userId!!,currentPage)
            }
            AppConstant.SUPPLIER -> {
                mViewModel.getCancelledList(role,userId!!,currentPage)
            }
        }
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter(AppConstant.CANCELLED)
    }

    private fun callAdapter(status: String) {
        val bookingsAdapter = EnquiryAdapter(requireActivity(), status,enquiryList,this)
        mainBinding.cancelRecyclerview.adapter = bookingsAdapter
    }

    override fun callEnquiryView(id: Int, pos: Int, action: String) {
        try {

            when(action){
                AppConstant.VIEW_ENQUIRY -> {
                    val data = enquiryList[pos]
                    callViewEnquiry(data)
                }
                AppConstant.RESTORE_INDENT -> {
                    mViewModel.callRestore(id,userId!!)
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}