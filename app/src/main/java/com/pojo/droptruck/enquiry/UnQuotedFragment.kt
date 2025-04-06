package com.pojo.droptruck.enquiry

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.EnquiryAdapter
import com.pojo.droptruck.databinding.FragmentUnQuotedBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callIndentEdit
import com.pojo.droptruck.utils.callViewEnquiry
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCurrencyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnQuotedFragment : BaseFragment(),EnquiryAdapter.EnquiryInterface {

    private lateinit var mBinding: FragmentUnQuotedBinding
    val mViewModel: EnquiryViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "UnQuotedFragment"
    var userId: String? = ""

    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        mBinding = FragmentUnQuotedBinding.inflate(inflater, container, false)

        Log.d(TAG, "onCreateView: ")

        mViewModel.resultLiveData.observe(viewLifecycleOwner, Observer {

            if (it!=null){
                AppUtils.dismissProgressDialog(progressDialog)
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
                            //enquiryList.clear()
                            enquiryList.addAll(it.data.indents) //becz of pagination...
                            callAdapter(AppConstant.UNQUOTED)
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
            AppUtils.dismissProgressDialog(progressDialog)
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
                                    currentPage = 1
                                    initFn()

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

        mViewModel.deleteLiveData.observe(viewLifecycleOwner,Observer {
            AppUtils.dismissProgressDialog(progressDialog)
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                currentPage = 1
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

        mViewModel.cloneIndentLiveData.observe(viewLifecycleOwner,Observer {
            AppUtils.dismissProgressDialog(progressDialog)
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                currentPage = 1
                                initFn()
                                /*if(it.data.response == 200) {
                                    initFn()
                                }*/
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
            mBinding.nestedSV.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener
                { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                    try{
                        if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                            currentPage++
                            if (currentPage <= totalPage) {
                                //call API...
                                initFn()
                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                })
        }catch (e:Exception){
            e.printStackTrace()
        }

        //initFn()
        return mBinding.root
    }

    private fun initFn() {
        progressDialog = AppUtils.showProgressDialog(requireActivity())
        userId = prefs.getValueString(AppConstant.USER_ID)
        val role = prefs.getValueString(AppConstant.ROLE_ID)

        when(role) {
            AppConstant.SALES -> {
                mViewModel.getEnquiryUnQuotedList(role,userId!!,currentPage)
            }
            AppConstant.SUPPLIER -> {
                mViewModel.getEnquiryUnQuotedList(role,userId!!,currentPage)
            }AppConstant.CUSTOMER -> {
                mViewModel.getCustomerList(role,userId!!,currentPage)
            }
        }
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter(AppConstant.UNQUOTED)
    }

    private fun callAdapter(status: String) {
        val bookingsAdapter = EnquiryAdapter(requireActivity(), status,enquiryList,this)
        mBinding.unquoteRecyclerview.adapter = bookingsAdapter
    }

    override fun callEnquiryView(id: Int, pos: Int,action:String) {
        try {

            when(action){
                AppConstant.EDIT_ENQUIRY -> {
                    val data = enquiryList[pos]
                    callIndentEdit(data)
                }
                AppConstant.VIEW_ENQUIRY -> {
                    val data = enquiryList[pos]
                    callViewEnquiry(data)
                }
                AppConstant.RATE_ENQUIRY -> {
                    showRateDialog(pos,id)
                }
                AppConstant.DELETE_ENQUIRY -> {
                    showConfirmationDialog(pos,id)
                }
                AppConstant.CLONE_INDENT -> {
                    showCloneConfirmationDialog(id.toString());

                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showConfirmationDialog(pos: Int, id: Int) {
        var alertDialog: AlertDialog? = null
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setTitle("Delete")
        alertDialogBuilder.setMessage("Are you sure you want to delete?")
        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            progressDialog = AppUtils.showProgressDialog(requireActivity())
            dialogInterface.cancel()
            val data = enquiryList[pos]
            mViewModel.deleteIndent(data.id)
        }
        alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        }
        alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
                progressDialog = AppUtils.showProgressDialog(requireActivity())

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        currentPage = 1
        clearData()
        initFn()
    }

    private fun showCloneConfirmationDialog(id: String) {
        var alertDialog: AlertDialog? = null
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setTitle("Create Indent")
        alertDialogBuilder.setMessage("Are you sure you want to clone this indent?")
        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel()
            progressDialog = AppUtils.showProgressDialog(requireActivity())
            mViewModel.callCloneIndent(id)
        }
        alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        }
        alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.deleteLiveData.value = null
        mViewModel.cloneIndentLiveData.value = null
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        /*mViewModel.resultLiveData.value = null
        mViewModel.applyRateResLiveData.value = null
        mViewModel.deleteLiveData.value = null
        mViewModel.cloneIndentLiveData.value = null*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

}