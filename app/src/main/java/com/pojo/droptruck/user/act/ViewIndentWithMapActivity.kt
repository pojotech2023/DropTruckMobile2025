package com.pojo.droptruck.user.act


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.pojo.droptruck.R
import com.pojo.droptruck.activity.confirmindent.ConIndentViewModel
import com.pojo.droptruck.databinding.ActivityViewIndentWithMapBinding
import com.pojo.droptruck.databinding.FromToLayBinding
import com.pojo.droptruck.databinding.TripDtlBottomSheetBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.DriverDetail
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.user.model.MapData
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callImageViewActivity
import com.pojo.droptruck.utils.callWebView
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCustRateDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class ViewIndentWithMapActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var bottomSheet: LinearLayout
    lateinit var tripDtlBinding: ActivityViewIndentWithMapBinding
    lateinit var mBinding: TripDtlBottomSheetBinding
    lateinit var locBinding: FromToLayBinding

    var userId: String = ""

    private var vehiclePhoto = ""
    private var driverLicense = ""
    private var rcBook = ""
    var insurance = ""

    var data: Indents? = Indents()
    var status = ""
    var from = ""
    var indentId: String=""
    val viewModel: ConIndentViewModel by viewModels()

    private lateinit var mMap: GoogleMap

    var fromLatLng:LatLng?=null
    var toLatLng:LatLng?=null
    var myTimer: Timer?=null
    lateinit var dialog: Dialog
    var role:String = ""
    private var sDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripDtlBinding = ActivityViewIndentWithMapBinding.inflate(layoutInflater)
        setContentView(tripDtlBinding.root)
        mBinding = tripDtlBinding.sheet
        locBinding = mBinding.loc

        role = prefs.getValueString(AppConstant.ROLE_ID)!!
        userId = prefs.getValueString(AppConstant.USER_ID)!!

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(mBinding.bottomSheet)

        mBinding.simpleView.visibility = View.VISIBLE
        mBinding.fullView.visibility = View.GONE

        mBinding.imgBack.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
                finish()
            }else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        }
        tripDtlBinding.imgBackNew.setOnClickListener { finish() }

        try {
            if (intent!=null){
                from = intent.getStringExtra("from").toString()

                if (from.isNotEmpty() && from.equals("map",true)) {
                    indentId = intent.getStringExtra(AppConstant.INDENT).toString()
                    showProgressDialog()
                    viewModel.callCustomerIndentDetails(indentId)
                }else {

                    data = (intent.getParcelableExtra("data") as? Indents)
                    status = intent.getStringExtra("status").toString()

                    indentId = data?.id.toString()

                    checkStatus(data)

                    setDatatoView(data)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        mBinding.vpLayout.setOnClickListener {
            callImageViewActivity(vehiclePhoto,resources.getString(R.string.vehicle_photo),data?.id.toString())
        }
        mBinding.dlLayout.setOnClickListener {
            callImageViewActivity(driverLicense,resources.getString(R.string.Driver_License),data?.id.toString())
        }
        mBinding.rcbLayout.setOnClickListener {
            callImageViewActivity(rcBook,resources.getString(R.string.RC_Book),data?.id.toString())
        }
        mBinding.insLayout.setOnClickListener {
            callImageViewActivity(insurance,resources.getString(R.string.Insurance),data?.id.toString())
        }

        mBinding.btnTrack.setOnClickListener {

            if(data!=null && data?.trackingLink!=null && data?.trackingLink?.isNotEmpty()!!) {
                callWebView(data?.trackingLink.toString())
            }else {
                shortToast("No Tracking Link Found")
            }
        }


        viewModel.customerIndentLiveData.observe(this, Observer {
            if (it!=null){
                dismissProgressDialog()
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data?.status == 200) {
                            if (it.data.data!=null && it.data.data.size>0) {
                                setDatatoView(it.data.data[0])
                            }
                        }else if (it.data?.status == 400) {
                            afterCancel()
                        }
                    }
                    Status.ERROR -> {
                        //shortToast(it.message?:"Something went wrong")
                    }
                }
            }
        })


        callTimer()

        //new...
        mBinding.btnConfirm.setOnClickListener {
            if (AppUtils.checkRate(data?.customerRate)){
                showProgressDialog()
                viewModel.callConfirmTrip(indentId,userId,role)
            }else {
                shortToast("Customer rate not given")
            }
        }
        viewModel.confirmTripLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                //shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    viewModel.callCustomerIndentDetails(indentId)
                                }
                            }else{
                                //shortToast("No Response")
                            }
                        }
                        Status.ERROR -> {
                            //shortToast(it.message!!)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        })

        mBinding.btnCancel.setOnClickListener {
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

            edtDate.setOnClickListener { callDatePicker(edtDate,AppConstant.FOLLOWUP) }

            dialog.show()

        }

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

        //#3 Listening to State Changes of BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("Sliding","Slide")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mBinding.simpleView.visibility = View.GONE
                        mBinding.fullView.visibility = View.VISIBLE
                        mBinding.fullView1.visibility = View.VISIBLE

                        // if status moved from waiting for driver then show it...
                        if (status.isNotEmpty()) {
                            if (status.equals("0") || status.equals("1")) {
                                hideDriverImgs()
                            }else {
                                viewDriverImgs()
                            }
                        }

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mBinding.simpleView.visibility = View.VISIBLE
                        mBinding.fullView.visibility = View.GONE
                        mBinding.fullView1.visibility = View.GONE
                    }
                    else -> {
                        Log.d("BottomSheet", "Persistent Bottom Sheet")
                    }
                }
            }
        })

    }

    private fun checkStatus(it: Indents?) {
        /*if (status.equals("0")) {
            mBinding.btnLay.visibility = View.VISIBLE
        }else {
            mBinding.btnLay.visibility = View.GONE
        }*/

        if (AppUtils.checkRate(it?.customerRate)) {
            if (it?.status.equals("0")) {
                mBinding.btnLay.visibility = View.VISIBLE
            }else{
                mBinding.btnLay.visibility = View.GONE
            }
            //mBinding.btnLay.visibility = View.VISIBLE
        }else{
            mBinding.btnLay.visibility = View.GONE
        }

    }

    private fun callTimer() {
        if (myTimer==null) {
            myTimer = Timer()
        }else {
            myTimer?.cancel()
            myTimer = Timer()
        }

        myTimer?.schedule(object : TimerTask() {
            override fun run() {
                //viewModel.callConfirmIndent(userId,indentId)
                viewModel.callCustomerIndentDetails(indentId)
                //Log.d("Calls","OKKKK")
            }
        }, 0,10000)
    }

    private fun setDatatoView(data: Indents?) {
        if (data!=null){
            this.data = data
            val enq = "DT"+data.id.toString()
            mBinding.txtEnq.text = enq
            mBinding.txtEnqs.text = enq
            mBinding.valEnqNum.text = enq
            //mBinding.valTruckType.text = data?.truckTypeName + ", " +data.bodyType

            var bodyType = ""
            try {
                if (data.bodyType.equals("Others",true)){
                    bodyType = data.newBodyType.toString()
                }else{
                    bodyType = data.bodyType.toString()
                }
            }catch (e:Exception) {
                e.printStackTrace()
            }

            if (data.truckTypeName.equals("Others",true)) {
                mBinding.valTruckType.text = data.newTruckType + ", "+bodyType
            }else{
                mBinding.valTruckType.text = data.truckTypeName + ", "+bodyType
            }

            val weight = data.weight + data.weightUnit

            if (data?.materialTypeName.equals("Others",true)) {
                mBinding.valMatType.text = data?.newMaterialType + ", " + weight
            }else{
                mBinding.valMatType.text = data?.materialTypeName + ", " + weight
            }

            try {
                mBinding.txtDate.text = AppUtils.convertDate(this.data?.createdAt.toString())
                mBinding.txtRate.text = data!!.customerRate
                mBinding.amt.text = data!!.customerRate
            }catch (e:Exception){
                e.printStackTrace()
            }

            locBinding.txtPick.text = data?.pickupLocationId
            locBinding.txtDrop.text = data?.dropLocationId


            if (data.driverDetails?.size!! >0) {
                data?.driverDetails?.get(0)?.let { setDriverDetails(it) }
            }

            try {

                mBinding.txtRate.text = data!!.customerRate
                mBinding.amt.text = data!!.customerRate

                if (data?.materialTypeName.equals("Others",true)) {
                    mBinding.valMatType.text = data?.newMaterialType
                }else{
                    mBinding.valMatType.text = data?.materialTypeName
                }

            }catch (e:Exception) {
                e.printStackTrace()
            }

            try {

                fromLatLng = getLocationFromAddress(data.pickupLocationId)
                toLatLng = getLocationFromAddress(data.dropLocationId)

                if (fromLatLng!=null && toLatLng!=null) {
                    Log.d("from",fromLatLng!!.latitude.toString() + " " +fromLatLng!!.longitude.toString())
                    Log.d("to",toLatLng!!.latitude.toString() + " " +toLatLng!!.longitude.toString())
                    onMapReady(mMap)
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

            status = data.status.toString()
            setStatus(mBinding.title,data)
            checkStatus(data)
            Log.d("Status:::",status)

            if (AppUtils.checkRate(data.customerRate)){
                mBinding.enqLay.visibility = View.GONE
                mBinding.rateLay.visibility = View.VISIBLE
            }else{
                //amount not there
                mBinding.enqLay.visibility = View.VISIBLE
                mBinding.rateLay.visibility = View.GONE
            }

        }
    }

    private fun setDriverDetails(driver: DriverDetail) {

        driver.let {

            try {
                mBinding.valDriverName.text = it.driverName
                mBinding.valDriverNum.text = it.driverNumber
                mBinding.valueDriverNum.text = it.driverNumber
                mBinding.valVehicleNum.text = it.vehicleNumber
                mBinding.valVehicelNum.text = it.vehicleNumber
            }catch (e:Exception){
                e.printStackTrace()
            }

            try {

                var images = it

                images.let {

                    vehiclePhoto = AppUtils.IMAGE_BASE_URL+it.vehiclePhoto
                    driverLicense = AppUtils.IMAGE_BASE_URL+it.driverLicense
                    rcBook = AppUtils.IMAGE_BASE_URL+it.rcBook
                    insurance = AppUtils.IMAGE_BASE_URL+it.insurance

                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            //bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun getLocationFromAddress(strAddress: String?): LatLng? {
        val coder = Geocoder(this)
        val address: List<Address>?
        var p1: LatLng? = null

        try {
            address = coder.getFromLocationName(strAddress!!, 1)
            if (address == null) {
                return null
            }
            val location: Address = address[0]

            p1 = LatLng(
                (location.latitude) as Double,
                (location.longitude) as Double
            )

            return p1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun setStatus(txtStatus: TextView, indent: Indents) {
        try {
            when(indent.status) {
                "0" -> {
                    if (AppUtils.checkRate(indent.customerRate)){
                        //txtStatus.text = "QUOTED"
                        txtStatus.text = resources.getString(R.string.wait_for_confirmation)
                    }else{
                        //txtStatus.text = "UNQUOTED"
                        txtStatus.text = resources.getString(R.string.wait_for_rate)
                    }
                    hideDriverImgs()
                }
                AppConstant.TRIPS_CONFIRMED -> {
                    txtStatus.text = "WAITING FOR DRIVER"
                    hideDriverImgs()
                }
                AppConstant.TRIPS_LOADING -> {
                    txtStatus.text = "LOADING"
                    viewDriverImgs()
                    visibleNotFullView()
                }
                AppConstant.TRIPS_UNLOADING -> {
                    txtStatus.text = "UNLOADING"
                    viewDriverImgs()
                    mBinding.btnTrack.visibility = View.VISIBLE
                    visibleNotFullView()
                }
                AppConstant.TRIPS_ONTHEROAD -> {
                    txtStatus.text = "ON THE ROAD"
                    viewDriverImgs()
                    mBinding.btnTrack.visibility = View.VISIBLE
                    visibleNotFullView()
                }
                AppConstant.TRIPS_POD -> {
                    txtStatus.text = "POD"
                    viewDriverImgs()
                    mBinding.btnTrack.visibility = View.VISIBLE
                    visibleNotFullView()
                }
                AppConstant.TRIPS_COMPLETED -> {
                    txtStatus.text = "COMPLETED"
                    viewDriverImgs()
                    mBinding.btnTrack.visibility = View.VISIBLE
                    visibleNotFullView()
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {
            if (indent.cancelReasons!=null && indent.cancelReasons.size>0) {
                txtStatus.text = "Cancelled"
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }

    }

    private fun visibleNotFullView() {
        mBinding.notFullView.visibility = View.VISIBLE
    }

    private fun viewDriverImgs() {
        mBinding.driverImg1.visibility = View.VISIBLE
        mBinding.driverImg2.visibility = View.VISIBLE
        mBinding.dropLocImg.visibility = View.VISIBLE
        bottomSheetBehavior.isDraggable = true
    }
    private fun hideDriverImgs() {
        mBinding.driverImg1.visibility = View.GONE
        mBinding.driverImg2.visibility = View.GONE
        mBinding.dropLocImg.visibility = View.GONE
        bottomSheetBehavior.isDraggable = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            if (fromLatLng!=null && toLatLng!=null) {
                mMap.addMarker(MarkerOptions().position(fromLatLng!!))
                mMap.addMarker(MarkerOptions().position(toLatLng!!))

                val url = getDirectionURL(fromLatLng!!, toLatLng!!, AppUtils.getMKStr())
                Log.d("url",url)
                GetDirection(url).execute()

                val latlngBuilder = LatLngBounds.Builder()

                latlngBuilder.include(fromLatLng!!)
                latlngBuilder.include(toLatLng!!)

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 200))

                try{
                    val dist = AppUtils.getDistance(fromLatLng!!,toLatLng!!)
                    mBinding.loc.txtKm.text = dist

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }


    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()

            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, MapData::class.java)

                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            try {
                val lineoption = PolylineOptions()
                for (i in result.indices){
                    lineoption.addAll(result[i])
                    lineoption.width(10f)
                    lineoption.color(Color.GREEN)
                    lineoption.geodesic(true)
                }
                mMap.addPolyline(lineoption)
            }catch (e:Exception){
                e.printStackTrace()
            }


        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }

    private fun cancelTimer() {
        try {
            myTimer?.cancel()
            myTimer = null
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun callCancelApi(cancelReason: String, reason: String, date: String,fReason:String) {
        showProgressDialog()
        viewModel.callCancelTrip(indentId,userId,cancelReason,reason,date,role,fReason)
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

    private fun callDatePicker(edtDate: EditText,from:String) {
        val calendar = Calendar.getInstance()
        val mYear: Int = calendar.get(Calendar.YEAR) // current year
        val mMonth: Int = calendar.get(Calendar.MONTH) // current month
        val mDay: Int = calendar.get(Calendar.DAY_OF_MONTH) // current day

        val dialog = DatePickerDialog(this@ViewIndentWithMapActivity, { _, year, month, day_of_month ->
            var cMonth: String = (month + 1).toString()
            if (month+1<10) {
                cMonth = "0" + (month + 1).toString()
            }

            var selDate: String = day_of_month.toString()
            if (day_of_month<10) {
                selDate = "0" + day_of_month.toString()
            }

            var date = ""
            //val date = "$day_of_month-$cMonth-$year"

            if (from.equals(AppConstant.CREATE_INDENT)) {
                date = "$selDate-$cMonth-$year"
            }else if (from.equals(AppConstant.FOLLOWUP)) {
                date = "$year-$cMonth-$selDate"
            }
            Log.d("date",date)
            edtDate.setText(date)
            sDate = date

        }, mYear, mMonth, mDay)

        dialog.datePicker.minDate = calendar.timeInMillis
        dialog.datePicker.fitsSystemWindows = true
        dialog.show()

    }

    private fun afterCancel() {
        shortToast("Indent Cancelled")
        finish()
    }

}