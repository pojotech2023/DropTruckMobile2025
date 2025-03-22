package com.pojo.droptruck.trips

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.pojo.droptruck.adapter.TripsAdapter
import com.pojo.droptruck.databinding.FragmentWaitingForDriverBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callCreateDriver
import com.pojo.droptruck.utils.callViewEnquiry
import com.pojo.droptruck.utils.callViewTripsDetails
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCurrencyDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class WaitingForDriverFragment : BaseFragment(), TripsAdapter.TripsInterface {

    lateinit var mainBinding: FragmentWaitingForDriverBinding
    val mViewModel: TripsViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "QuotedFragment"
    var userId: String = ""
    var role:String = ""

    val viewModel: ConIndentViewModel by viewModels()
    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mainBinding = FragmentWaitingForDriverBinding.inflate(inflater, container, false)

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

        viewModel.cancelTripLiveData.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
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
        userId = prefs.getValueString(AppConstant.USER_ID)!!
        role = prefs.getValueString(AppConstant.ROLE_ID)!!

        mViewModel.callConfirmedTrips(userId,role,currentPage)
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter()
    }

    private fun callAdapter() {
        val tripsAdapter = TripsAdapter(requireActivity(), AppConstant.WFDRIVER,enquiryList,this)
        mainBinding.tripsRecyclerview.adapter = tripsAdapter
    }

    override fun callTripsView(id: Int, pos: Int, action: String) {

        when(action) {
            AppConstant.CREATE_DRIVER -> {
                callCreateDriver(id)
            }
            AppConstant.LOSS -> {
            callCancelDailog(id)
        }
            AppConstant.VIEW_ENQUIRY -> {
                val data = enquiryList[pos]
                callViewTripsDetails(data)
            }
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
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)

                    if(position>0) {

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

                    }else {
                        cancelReason = ""
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            btnSubmit.setOnClickListener {

                if (id.toString().isNotEmpty()) {

                    if (spinner.selectedItemPosition >0){

                        if (cancelReason.equals("Others",true)) {

                            if (edtReason.text.toString().trim().isEmpty()){
                                shortToast("Enter Reason")
                            }else {
                                callCancelApi(cancelReason,edtReason.text.toString(),"",
                                    id.toString(),"")
                            }

                        }else if (cancelReason.equals("Followup",true)) {

                            if (edtDate.text.toString().trim().isEmpty()){
                                shortToast("Select date")
                            }else if (edtFollowup.text.toString().trim().isEmpty()){
                                shortToast("Enter Follow up Remarks")
                            }else {
                                callCancelApi(cancelReason,"",edtDate.text.toString()
                                    ,id.toString(),edtFollowup.text.toString().trim())
                            }
                        }else {
                            callCancelApi(cancelReason,"","",id.toString(),"")
                        }

                    }else {
                        shortToast("please select cancel reason")
                    }
                }

            }

            imgClose.setOnClickListener {
                closeDialog()
            }

            edtDate.setOnClickListener { callDatePicker(edtDate) }

            dialog.show()

        }catch (e:Exception){
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

    private fun callCancelApi(cancelReason: String, reason: String, date: String,indentId:String,
                              fReason:String) {
        showProgressDialog()
        viewModel.callCancelTrip(indentId,userId,cancelReason,reason,date,role,fReason)
    }


}