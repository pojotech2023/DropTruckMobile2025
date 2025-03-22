package com.pojo.droptruck.activity.confirmindent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.AlgorithmAdapter
import com.pojo.droptruck.databinding.ActivityConfirmIndentBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.IndentDetails
import com.pojo.droptruck.pojo.IndentRate
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.pojo.User
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCustRateDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class ConfirmIndentActivity : BaseActivity() {

    lateinit var binding: ActivityConfirmIndentBinding
    val viewModel: ConIndentViewModel by viewModels()
    var indentId: String=""
    lateinit var dialog: Dialog
    var userId:String = ""
    var role:String = ""

    var adapter: AlgorithmAdapter? = null
    val TAG = "ConfirmIndentActivity"
    var rateId = ""

    lateinit var data: Indents

    var driverSpinnerTouch = false
    private var isRateUpdate = false
    private var isCustomerRateAPICalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmIndentBinding.inflate(layoutInflater)
        //binding.obj = viewModel
        //binding.lifecycleOwner = this
        setContentView(binding.root)

        userId = prefs.getValueString(AppConstant.USER_ID).toString()
        role = prefs.getValueString(AppConstant.ROLE_ID).toString()

        binding.imgLay.setOnClickListener { finish() }

        try {
            if (intent!=null) {
                indentId = intent.getStringExtra("id")!!
                
                if (role.equals(AppConstant.CUSTOMER)) {
                    binding.cRateLay.visibility = View.VISIBLE
                    try {

                        data = (intent.getParcelableExtra("data") as? Indents)!!
                        Log.d("Data: ", Gson().toJson(data))

                        if (::data.isInitialized) {

                            binding.txtPloc.text = data.pickupLocationId
                            binding.txtDloc.text = data.dropLocationId
                            binding.txtTt.text = data.truckTypeName
                            binding.txtBt.text = data.bodyType
                            binding.txtWeight.text = data.weight + " " + data.weightUnit
                            //binding.txtMt.text = data.materialTypeId

                            binding.txtCustomerRate.text = data.customerRate

                            setSpinnerValue(data.driverRate)
                        }
                        
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    
                }else if (role.equals(AppConstant.SALES)) {
                    binding.cRateLay.visibility = View.VISIBLE
                }
                
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        if (role.equals(AppConstant.CUSTOMER)) {
            binding.btnCustomerRate.visibility = View.GONE
            binding.deleteRate.visibility = View.GONE
            binding.driverRateLay.visibility = View.GONE
        }else {
            callIntentDetailsAPI()
        }

        viewModel.resultLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {

                        if (it.data?.indentDetails!=null){
                            //viewModel.setIndentDetails(it.data.indentDetails)

                            if (!isRateUpdate) {
                                if (it.data.indentRates!=null &&
                                    it.data.indentRates.size>0) {

                                    val indentRate = ArrayList<IndentRate>()

                                    var iRate = IndentRate()
                                    var mUser = User()
                                    mUser.name = "Rate"
                                    iRate.id = 0
                                    iRate.name = "Select"
                                    iRate.rate = "Select"
                                    iRate.user = mUser
                                    indentRate.add(iRate)
                                    indentRate.addAll(it.data.indentRates)

                                    adapter = AlgorithmAdapter(this, indentRate,AppConstant.ConfirmIndentActivity)
                                    binding.driverRateSpinner.adapter = adapter

                                    setSelectedDriverRate(it.data.indentRates)

                                }
                            }

                            setValues(it.data.indentDetails)

                        }

                    }
                    Status.ERROR -> {
                        shortToast(it.message?:"Something went wrong")
                    }
                }
            }
        })

        viewModel.confirmDriverLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                closeDialog()
                                shortToast(it.data.message!!)
                                binding.driverRateSpinner.isEnabled = false
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

        viewModel.rateUpdateLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                closeDialog()
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    binding.cRateLay.visibility = View.VISIBLE
                                    isCustomerRateAPICalled = true
                                    isRateUpdate = true
                                    callIntentDetailsAPI()
                                }else{
                                    shortToast(it.data.message!!)
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

        viewModel.deleteRateLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    binding.driverRateSpinner.isEnabled = true
                                    callIntentDetailsAPI()
                                }/*else{
                                    shortToast(it.data.message!!)
                                }*/
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

        viewModel.confirmTripLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    finish()
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

        viewModel.cancelTripLiveData.observe(this, Observer {
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
                                    finish()
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

        binding.btnCustomerRate.setOnClickListener {
            dialog = showCustRateDialog(R.layout.dialog_submit_rate)
            val btnSubmit: Button = dialog.findViewById(R.id.btn_save)
            val edtRate: EditText = dialog.findViewById(R.id.et_rate)

            btnSubmit.setOnClickListener {

                if (indentId.isNotEmpty()) {
                    if (edtRate.text.toString().trim().isNotEmpty()) {
                        showProgressDialog()
                        viewModel.callSubmitRate(indentId,edtRate.text.toString().trim(),userId,role)
                    }else {
                        shortToast("Please enter amount")
                    }
                }

            }

            dialog.show()
        }

        binding.btnCancelIndent.setOnClickListener {
            dialog = showCustRateDialog(R.layout.dialog_cancel_enquiry)
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

                if (indentId.isNotEmpty()) {

                    if (spinner.selectedItemPosition >0){

                        if (cancelReason.equals("Others",true)) {

                            if (edtReason.text.toString().trim().isEmpty()){
                                shortToast("Enter Reason")
                            }else {
                                callCancelApi(cancelReason,edtReason.text.toString(),"","")
                            }

                        }else if (cancelReason.equals("Followup",true)) {

                            if (edtDate.text.toString().trim().isEmpty()){
                                shortToast("Select date")
                            }else if (edtFollowup.text.toString().trim().isEmpty()){
                                shortToast("Enter Follow up Remarks")
                            }else {
                                callCancelApi(cancelReason,"",edtDate.text.toString(),
                                    edtFollowup.text.toString().trim())
                            }
                        }else {
                            callCancelApi(cancelReason,"","","")
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

        }

        binding.driverRateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                // It returns the clicked item.

                try {
                    if (position>0) {
                        val clickedItem: IndentRate =
                            parent.getItemAtPosition(position) as IndentRate
                        rateId = clickedItem.id.toString()
                        //var uId = clickedItem.userId.toString()

                        Log.d(TAG, "Select: $rateId")

                        if (driverSpinnerTouch) {
                            showProgressDialog()
                            viewModel.callSelectDriverRate(indentId,rateId,userId)
                        }

                        //viewModel.callSelectDriverRate(indentId,rateId,uId)

                    }else {
                        Log.d(TAG, "Select only: ")
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.deleteRate.setOnClickListener {
            showProgressDialog()
            viewModel.deleteSelectedAmt(indentId,userId)
        }

        binding.winBtn.setOnClickListener {
            if (binding.driverRateSpinner.selectedItemPosition>0) {
                if (isRateUpdate) {
                    showProgressDialog()
                    viewModel.callConfirmTrip(indentId,userId,role)
                    //shortToast("done ")
                }else{
                    shortToast("Please enter customer rate ")
                }
            }else {
                shortToast("Please choose driver rate ")
            }
        }

        try{

            binding.driverRateSpinner.setOnTouchListener { v, event ->
                Log.d(TAG, "onTouch: "+event?.action)
                when (event?.action) {

                    MotionEvent.ACTION_DOWN -> {
                        driverSpinnerTouch = true
                        Log.d(TAG, "onTouch: "+"Down")
                    }
                }

                v?.onTouchEvent(event) ?: true
            }

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun setSelectedDriverRate(indentRates: ArrayList<IndentRate>) {
        try {
            if(indentRates!=null && indentRates.size>0) {
                for (i in 0 until  indentRates.size) {
                    if (indentRates.get(i).isConfirmedRate == 1) {
                        binding.driverRateSpinner.setSelection(i+1)
                        binding.driverRateSpinner.isEnabled = false
                        break
                    }else {
                        if (i == indentRates.size-1){
                            binding.driverRateSpinner.isEnabled = true
                        }
                    }
                }
            }else{
                binding.driverRateSpinner.isEnabled = true
            }
        }catch (e:Exception){
            Log.d("Spinner Err",e.message.toString())
            e.printStackTrace()
        }
    }

    private fun setSpinnerValue(driverRate: String?) {

        try {

            val indentRate = ArrayList<IndentRate>()

            var iRate = IndentRate()
            iRate.id = 0
            iRate.name = ""
            iRate.rate = driverRate
            iRate.user = null

            indentRate.add(iRate)

            adapter = AlgorithmAdapter(this, indentRate,AppConstant.ConfirmIndentActivity)
            binding.driverRateSpinner.adapter = adapter

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun setValues(indentDetails: IndentDetails) {
        //viewModel.setIndentDetails(indentDetails)
        binding.txtPloc.text = indentDetails.pickupLocation
        binding.txtDloc.text = indentDetails.dropLocation
        binding.txtTt.text = indentDetails.truckType
        binding.txtBt.text = indentDetails.bodyType
        binding.txtWeight.text = indentDetails.weight + " " + indentDetails.weightUnit
        binding.txtMt.text = indentDetails.materialType
        binding.txtPerson.text = indentDetails.salesPerson

        binding.txtCustomerRate.text = indentDetails.customerRate

        if (!isCustomerRateAPICalled) {
            if (AppUtils.checkRate(indentDetails.customerRate)){
                isRateUpdate = true
            }else {
                isRateUpdate = false
            }
        }

    }

    private fun callCancelApi(cancelReason: String, reason: String, date: String,fReason:String) {
        showProgressDialog()
        viewModel.callCancelTrip(indentId,userId,cancelReason,reason,date,role,fReason)
    }

    private fun callIntentDetailsAPI() {
        showProgressDialog()
        if (indentId.isNotEmpty()){
            viewModel.callConfirmIndent(userId,indentId)
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

        val dialog = DatePickerDialog(this@ConfirmIndentActivity, { _, year, month, day_of_month ->

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

}