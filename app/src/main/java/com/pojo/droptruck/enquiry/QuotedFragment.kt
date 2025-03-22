package com.pojo.droptruck.enquiry

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.EnquiryAdapter
import com.pojo.droptruck.databinding.FragmentQuotedBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callConfirmIndents
import com.pojo.droptruck.utils.callViewEnquiry
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCurrencyDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuotedFragment : BaseFragment(),EnquiryAdapter.EnquiryInterface{

    lateinit var mainBinding: FragmentQuotedBinding
    val mViewModel: EnquiryViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "QuotedFragment"
    var userId: String? = ""
    var role: String? = ""
    var currentPage:Int = 1
    var totalPage:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainBinding = FragmentQuotedBinding.inflate(inflater, container, false)

        Log.d(TAG, "onCreateView: ")

        mViewModel.resultLiveData.observe(viewLifecycleOwner, Observer {
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

                        if (it.data?.indents != null &&  it.data.indents.size>0){
                            if (currentPage == 1){
                                enquiryList.clear()
                            }

                            enquiryList.addAll(it.data.indents) //becz of pagination...
                            callAdapter(AppConstant.QUOTED)
                        }else {
                            clearData()
                        }
                    }
                    Status.ERROR -> {
                        clearData()
                        //shortToast(it.message.toString())
                    }
                }
            }
        })

        mViewModel.applyRateResLiveData.observe(viewLifecycleOwner,Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                try {
                                    if (this::dialog.isInitialized) {
                                        dialog.dismiss()
                                        dialog.cancel()
                                    }
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                                shortToast(it.data.message!!)
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

        /*mainBinding.quoteRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLastItemReached && lastVisibleItemPosition == totalItemCount - 1) {
                    Log.d(TAG, "onScrolled: "+lastVisibleItemPosition)
                    if (currentPage != totalPage) {
                        //call API...
                        currentPage?.inc()
                        initFn()
                    }

                    // RecyclerView has reached the last item
                    Toast.makeText(recyclerView.context, "Reached last item", Toast.LENGTH_SHORT).show()
                    //recyclerView.postDelayed({ isLastItemReached = false }, 2000)
                }
            }
        })*/

        try{
            mainBinding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener
            { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                    try{
                        currentPage++
                        if (currentPage <= totalPage) {
                            //call API...
                            initFn()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            })
        }catch (e:Exception){
            e.printStackTrace()
        }

        initFn()

        return mainBinding.root
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter(AppConstant.QUOTED)
    }

    private fun callAdapter(status: String) {

        when(role) {
            AppConstant.SALES -> {
                if (enquiryList.size>0) {
                    //var oldList = ArrayList<Indents>()
                    //oldList.addAll(enquiryList)
                    try {
                        //enquiryList.clear()
                        //Log.d("List Size old ","" +oldList.size)
                        //enquiryList.addAll(oldList.sortedBy { it.customerRate?.toDouble() } )
                        enquiryList.sortBy { it.customerRate?.toDouble() }
                        Log.d("List Size ","" +enquiryList.size)
                        setData(status)
                    }catch (e:Exception) {
                        e.printStackTrace()
                        //enquiryList.clear()
                        //enquiryList.addAll(oldList)
                        Log.d("List Size exp ","" +enquiryList.size)
                        setData(status)
                    }

                }else {
                    setData(status)
                }
            }
            AppConstant.SUPPLIER -> {
                setData(status)
                /*if (enquiryList.size>0) {

                    try{

                        for (i in 0 until enquiryList.size) {
                            try {
                                enquiryList[i].leastAmt = (AppUtils.getLeastAmountForSupplier
                                    (enquiryList[i].indentRate,prefs.getValueString(AppConstant.USER_ID))).toDouble()
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }

                        enquiryList.sortBy { it.leastAmt }
                        setData(status)

                    }catch (e:Exception){
                        setData(status)
                    }

                }else {
                    setData(status)
                }*/
            }

        }

    }

    private fun setData(status: String) {
        val bookingsAdapter = EnquiryAdapter(requireActivity(), status,enquiryList,this)
        mainBinding.quoteRecyclerview.adapter = bookingsAdapter
    }

    override fun callEnquiryView(id: Int, pos: Int,action:String) {
        try {
            when(action){
                AppConstant.RATE_ENQUIRY -> { showRateDialog(pos,id) }
                AppConstant.CONFIRM_INDENT -> {
                    //showRateDialog(pos, id)
                    val data = enquiryList[pos]
                    callConfirmIndents(id.toString(),data)
                }
                AppConstant.VIEW_ENQUIRY -> {
                    val data = enquiryList[pos]
                    callViewEnquiry(data,"")
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun showRateDialog(pos: Int,id:Int) {
        dialog = showCurrencyDialog(R.layout.dialog_create_rate)
        //dialog.setContentView(R.layout.dialog_create_rate)

        val btnSubmit: Button = dialog.findViewById(R.id.btn_save)
        val edtEnqNo: EditText = dialog.findViewById(R.id.et_enqNo)
        val edtRate: EditText = dialog.findViewById(R.id.et_rate)
        val edtRemarks: EditText = dialog.findViewById(R.id.et_remarks)
        val edtTransporter: EditText = dialog.findViewById(R.id.et_transport)

        try{
            edtEnqNo.setText(enquiryList[pos].id.toString())
        }catch (e:Exception){
            e.printStackTrace()
        }

        btnSubmit.setOnClickListener {

            val rate = edtRate.text.toString()
            val transport = edtTransporter.text.toString()
            val remarks = edtRemarks.text.toString()

            if (edtRate.text.toString().trim().isEmpty()) {
                edtRate.error = "Please enter rate"
            }/*else if (edtTransporter.text.toString().trim().isEmpty()) {
                edtTransporter.error = "Please enter transporter"
            }else if (edtRemarks.text.toString().trim().isEmpty()) {
                edtRemarks.error = "Please enter remarks"
            }*/else {
                showProgressDialog()

                val applyRate = ApplyRate()
                applyRate.rate = rate
                applyRate.remarks = remarks
                applyRate.indentId = id.toString()
                applyRate.name = transport

                mViewModel.callApplyRate(applyRate,userId!!)
            }

        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //initFn()
    }

    private fun initFn() {
        showProgressDialog()
        userId = prefs.getValueString(AppConstant.USER_ID)
        role = prefs.getValueString(AppConstant.ROLE_ID)

        when(role) {
            AppConstant.SALES -> {
                mViewModel.getSupplierQuotedList(role!!,userId!!,currentPage)
            }
            AppConstant.SUPPLIER -> {
                mViewModel.getSupplierQuotedList(role!!,userId!!,currentPage)
            }
            AppConstant.CUSTOMER -> {
                mViewModel.getCustomerQuotedList(role!!,userId!!,currentPage)
            }
        }
    }

}