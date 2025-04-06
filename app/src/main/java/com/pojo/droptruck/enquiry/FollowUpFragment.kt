package com.pojo.droptruck.enquiry

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.pojo.droptruck.R
import com.pojo.droptruck.activity.confirmindent.ConIndentViewModel
import com.pojo.droptruck.adapter.EnquiryAdapter
import com.pojo.droptruck.databinding.FragmentCancelledEnquiryBinding
import com.pojo.droptruck.databinding.FragmentFollowUpBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callViewEnquiry
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCurrencyDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class FollowUpFragment : BaseFragment(),EnquiryAdapter.EnquiryInterface {

    lateinit var mainBinding: FragmentFollowUpBinding
    val mViewModel: EnquiryViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "CancelledEnquiryFragment"
    var userId: String = ""
    var role:String = ""
    val viewModel: ConIndentViewModel by viewModels()

    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainBinding = FragmentFollowUpBinding.inflate(inflater, container, false)

        mViewModel.followUpListLiveData.observe(viewLifecycleOwner, Observer {

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

                        if (it.data?.dataIndents != null &&  it.data.dataIndents.size>0){
                            if (currentPage == 1){
                                enquiryList.clear()
                            }
                            //enquiryList.clear()
                            enquiryList.addAll(it.data.dataIndents)
                            callAdapter(AppConstant.FOLLOWUP)
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

        viewModel.cancelTripLiveData.observe(viewLifecycleOwner, Observer {
            AppUtils.dismissProgressDialog(progressDialog)
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                closeDialog()
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    currentPage = 1
                                    initFn()
                                }
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
        }catch (e:Exception){
            e.printStackTrace()
        }

        //initFn() //becz of pagination...

        return mainBinding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        currentPage = 1
        clearData()
        initFn()
    }

    private fun initFn() {
        progressDialog = AppUtils.showProgressDialog(requireActivity())
        userId = prefs.getValueString(AppConstant.USER_ID)!!
        role = prefs.getValueString(AppConstant.ROLE_ID)!!

        mViewModel.getFollowUpList(role!!,userId!!,currentPage)
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter(AppConstant.FOLLOWUP)
    }

    private fun callAdapter(status: String) {
        val bookingsAdapter = EnquiryAdapter(requireActivity(), status,enquiryList,this)
        mainBinding.followRecyclerview.adapter = bookingsAdapter
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
                AppConstant.LOSS -> {
                    callCancelDailog(id)
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun callCancelDailog(id: Int) {

        try {

            dialog = showCurrencyDialog(R.layout.dialog_cancel_enquiry)
            val btnSubmit: Button = dialog.findViewById(R.id.btn_save)
            val spinner: Spinner = dialog.findViewById(R.id.cancelReasonSpinner)
            val tilCancelLay: TextInputLayout = dialog.findViewById(R.id.til_cancel_reason)
            val tilFollowupLay: TextInputLayout = dialog.findViewById(R.id.til_followup_reason)
            val tilDateLay: TextInputLayout = dialog.findViewById(R.id.til_date)
            var edtDate: EditText = dialog.findViewById(R.id.et_date)
            val edtReason: EditText = dialog.findViewById(R.id.et_cancel_reason)
            val edtFollowup: EditText = dialog.findViewById(R.id.et_followup)
            val imgClose: ImageView = dialog.findViewById(R.id.img_close)

            var cancelReason = ""

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)

                    if (position > 0) {

                        cancelReason = parent.selectedItem.toString()

                        if (cancelReason.equals("Others",true)){
                            tilCancelLay.visibility = View.VISIBLE
                            tilFollowupLay.visibility = View.GONE
                        }else if (cancelReason.equals("Followup",true)){
                            tilDateLay.visibility = View.VISIBLE
                            tilFollowupLay.visibility = View.VISIBLE
                            tilCancelLay.visibility = View.GONE
                        }else {
                            tilCancelLay.visibility = View.GONE
                            tilDateLay.visibility = View.GONE
                            tilFollowupLay.visibility = View.GONE
                        }

                    } else {
                        cancelReason = ""
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            btnSubmit.setOnClickListener {

                if (id.toString().isNotEmpty()) {

                    if (spinner.selectedItemPosition > 0) {

                        if (cancelReason.equals("Others", true)) {

                            if (edtReason.text.toString().trim().isEmpty()) {
                                shortToast("Enter Reason")
                            }else {
                                callCancelApi(
                                    cancelReason,
                                    edtReason.text.toString(),
                                    "",
                                    id.toString(),""
                                )
                            }

                        } else if (cancelReason.equals("Followup", true)) {

                            if (edtDate.text.toString().trim().isEmpty()) {
                                shortToast("Select date")
                            }else if (edtFollowup.text.toString().trim().isEmpty()){
                                shortToast("Enter Follow up Remarks")
                            }else {
                                callCancelApi(
                                    cancelReason,
                                    "",
                                    edtDate.text.toString(),
                                    id.toString(),edtFollowup.text.toString().trim()
                                )
                            }
                        } else {
                            callCancelApi(cancelReason, "", "", id.toString(),"")
                        }

                    } else {
                        shortToast("please select cancel reason")
                    }
                }

            }

            imgClose.setOnClickListener {
                closeDialog()
            }

            edtDate.setOnClickListener { callDatePicker(edtDate) }

            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun closeDialog() {
        try {
            if (this::dialog.isInitialized) {
                dialog.dismiss()
                dialog.cancel()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun callDatePicker(edtDate: EditText) {
        val calendar = Calendar.getInstance()
        val mYear: Int = calendar.get(Calendar.YEAR) // current year
        val mMonth: Int = calendar.get(Calendar.MONTH) // current month
        val mDay: Int = calendar.get(Calendar.DAY_OF_MONTH) // current day

        val dialog = DatePickerDialog(requireActivity(), { _, year, month, day_of_month ->

            var cMonth: String = (month + 1).toString()
            if (month+1<10) {
                cMonth = "0" + (month + 1).toString()
            }

            var selDate: String = day_of_month.toString()
            if (day_of_month<10) {
                selDate = "0" + day_of_month.toString()
            }

            //val date = "$cMonth/$day_of_month/$year"
            val date = "$year-$cMonth-$selDate"
            Log.d("date",date)
            edtDate.setText(date)

        }, mYear, mMonth, mDay)

        dialog.datePicker.minDate = calendar.timeInMillis
        dialog.datePicker.fitsSystemWindows = true
        dialog.show()

    }

    private fun callCancelApi(cancelReason: String, reason: String, date: String,indentId:String
                              ,fReason:String) {
        progressDialog = AppUtils.showProgressDialog(requireActivity())
        viewModel.callCancelTrip(indentId,userId,cancelReason,reason,date,role,fReason)
    }



}