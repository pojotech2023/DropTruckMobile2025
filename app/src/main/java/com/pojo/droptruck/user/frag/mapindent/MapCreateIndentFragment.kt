package com.pojo.droptruck.user.frag.mapindent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.pojo.droptruck.R
import com.pojo.droptruck.activity.confirmindent.ConIndentViewModel
import com.pojo.droptruck.databinding.FragmentMapCreateIndentBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.pojo.SpinnerData
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.prefs
import com.pojo.droptruck.user.model.MapData
import com.pojo.droptruck.user.model.UserCreateIndent
import com.pojo.droptruck.user.viewmodel.UserIndentsViewModel
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callCustomerViewTripsDetails
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCustRateDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class MapCreateIndentFragment : BaseFragment(),OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mBinding: FragmentMapCreateIndentBinding

    val mViewModel: UserIndentsViewModel by viewModels()

    var sourceOfLead = ""
    var pickUpLoc = ""
    var dropLoc = ""
    var truckType = ""
    var bodyType = ""
    var weightUnit = ""
    var matType = ""
    var payType = ""
    var pod = ""

    lateinit var data: Indents
    private var sDate = ""

    var newMatType = ""
    var newTruckType = ""
    var newBodyType = ""

    private lateinit var pickUpAutocompleteFragment: AutocompleteSupportFragment
    private lateinit var dropAutocompleteFragment: AutocompleteSupportFragment

    private var pickUpCity = ""
    private var dropCity = ""
    var userId:String = ""
    var indentId: String=""
    val viewModel: ConIndentViewModel by viewModels()
    var myTimer: Timer?=null
    var role:String = ""
    lateinit var dialog: Dialog

    var sydney: LatLng = LatLng(10.6609, 77.0048)
    var TamWorth: LatLng = LatLng(28.7041, 77.1025)
    lateinit var locationArrayList: ArrayList<LatLng>

    var originLatitude: Double = 0.0
    var originLongitude: Double = 0.0
    var destinationLatitude: Double = 0.0
    var destinationLongitude: Double = 0.0

    lateinit var originLocation: LatLng
    lateinit var destinationLocation: LatLng

    var fromLatLng:LatLng?=null
    var toLatLng:LatLng?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentMapCreateIndentBinding.inflate(inflater, container, false)

        locationArrayList = ArrayList()
        locationArrayList.add(sydney)
        locationArrayList.add(TamWorth)

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        userId = prefs.getValueString(AppConstant.USER_ID).toString()
        role = prefs.getValueString(AppConstant.ROLE_ID).toString()

        //setInitView()

        if(!Places.isInitialized()){
            context?.let { context ->
                Places.initialize(context, AppUtils.getMKStr())
            }
        }

        pickUpAutocompleteFragment =
            childFragmentManager.findFragmentById(R.id.pickupAutocomplete) as AutocompleteSupportFragment

        pickUpAutocompleteFragment.setPlaceFields(listOf(
            Place.Field.LAT_LNG, Place.Field.ID,
            Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS))

        dropAutocompleteFragment =
            childFragmentManager.findFragmentById(R.id.dropAutocomplete) as AutocompleteSupportFragment
        dropAutocompleteFragment.setPlaceFields(listOf(
            Place.Field.LAT_LNG, Place.Field.NAME,
            Place.Field.ADDRESS, Place.Field.ID, Place.Field.ADDRESS_COMPONENTS))


        pickUpAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: com.google.android.gms.common.api.Status) {
                println("ErrorReceived")
                println(p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {

                getLatLong(p0.latLng);
                println("GotPlaces " +p0.name)
                //pickUpLoc = p0.name.toString()
                pickUpLoc = p0.address
                fetchPlaceDetails(p0.id,AppConstant.PICKUP_LOC)
                getCityName(p0.latLng,AppConstant.PICKUP_LOC)
            }

        })


        dropAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: com.google.android.gms.common.api.Status) {
                println("ErrorReceived")
                println(p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {

                getLatLong(p0.latLng);
                println("GotPlaces "+p0.name)
                //dropLoc = p0.name.toString()
                dropLoc = p0.address
                fetchPlaceDetails(p0.id,AppConstant.DROP_LOC)
                getCityName(p0.latLng,AppConstant.DROP_LOC)
            }

        })

        try {
            if (::data.isInitialized) {
                pickUpAutocompleteFragment.setText(data.pickupLocationId)
                dropAutocompleteFragment.setText(data.dropLocationId)
                pickUpLoc = data.pickupLocationId.toString()
                dropLoc = data.dropLocationId.toString()

            }
        }catch (e:Exception){
            e.printStackTrace()
        }


        mViewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                shortToast(it)
            }
        })

        mViewModel.mLoader.observe(viewLifecycleOwner, Observer {
            if (it!=null) {
                if (it){
                    showProgressDialog()
                    shortToast("Success")
                }else {
                    dismissProgressDialog()
                }
            }
        })

        mBinding.truckTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                //truckType = position.toString()

                try{
                    val selectedObject = mBinding.truckTypeSpinner.selectedItem as SpinnerData
                    truckType = selectedObject.id.toString() //new...
                    Log.d("Item: ", "selected item " + truckType)
                }catch (e:Exception){
                    truckType = position.toString() //old...
                    e.printStackTrace()
                }

                if (position>0 && !parent.selectedItem.toString().equals("Others",true)) {
                    newTruckType = ""
                    mBinding.truckLay.visibility = View.GONE
                }else if (parent.selectedItem.toString().equals("Others",true)){
                    mBinding.truckLay.visibility = View.VISIBLE
                }else{
                    newTruckType = ""
                    mBinding.truckLay.visibility = View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        mBinding.bodyTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                bodyType = parent.selectedItem.toString()

                if (position>0 && !parent.selectedItem.toString().equals("Others",true)) {
                    newBodyType = ""
                    mBinding.bodyTypeLay.visibility = View.GONE
                }else if (parent.selectedItem.toString().equals("Others",true)){
                    mBinding.bodyTypeLay.visibility = View.VISIBLE
                }else{
                    newBodyType = ""
                    bodyType = ""
                    mBinding.bodyTypeLay.visibility = View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        mBinding.weightSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                weightUnit = parent.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        mBinding.materialTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                var pos = position +1

                //matType = position.toString()

                try{
                    val selectedObject = mBinding.materialTypeSpinner.selectedItem as SpinnerData
                    matType = selectedObject.id.toString() //new...
                    Log.d("Item: ", "selected item " + matType)
                }catch (e:Exception){
                    matType = position.toString() //old...
                    e.printStackTrace()
                }

                if (position>0 && !parent.selectedItem.toString().equals("Others",true)) {
                    newMatType = ""
                    mBinding.matLay.visibility = View.GONE
                }else if (parent.selectedItem.toString().equals("Others",true)){
                    mBinding.matLay.visibility = View.VISIBLE
                }else{
                    //matType = ""
                    newMatType = ""
                    mBinding.matLay.visibility = View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mBinding.btnSubmit.setOnClickListener {

            if (pickUpLoc.isEmpty()){
                shortToast("Select Pickup Location")
            }else if (dropLoc.isEmpty()){
                shortToast("Select Drop Location")
            }else if (mBinding.truckTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Truck Type")
            }else if (mBinding.truckTypeSpinner.selectedItem.toString().equals("Others",true)
                && mBinding.etTruckType.text.toString().trim().isEmpty()){
                shortToast("Please Enter truck type")
            } else if (mBinding.bodyTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Body Type")
            }else if (mBinding.bodyTypeSpinner.selectedItem.toString().equals("Others",true)
                && mBinding.etBodyType.text.toString().trim().isEmpty()){
                shortToast("Please Enter body type")
            }else if (mBinding.materialTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Material Type")
            }else if (mBinding.etWeight.text.toString().trim().isEmpty()){
                shortToast("Enter Weight")
            }/*else if (mBinding.weightSpinner.selectedItemPosition == 0){
                shortToast("Select Weight")
            }*/else {

                showProgressDialog()
                val cInt = UserCreateIndent()
                val userId = prefs.getValueString(AppConstant.USER_ID)

                cInt.userId = userId.toString()
                cInt.pickUpLocation = pickUpLoc
                cInt.dropLocation = dropLoc
                cInt.truckType = truckType
                cInt.bodyType = bodyType
                cInt.weight = mBinding.etWeight.text.toString().trim()
                cInt.weightUnit = weightUnit
                cInt.reqDate = sDate
                cInt.matType = matType

                if (mBinding.etMatType.text.toString().trim().isNotEmpty()) {
                    newMatType = mBinding.etMatType.text.toString().trim()
                }else {
                    newMatType = ""
                }

                newBodyType = mBinding.etBodyType.text.toString().trim()
                newTruckType = mBinding.etTruckType.text.toString().trim()

                cInt.new_body_type = newBodyType
                cInt.new_truck_type = newTruckType
                cInt.new_material_type = newMatType

                cInt.pickup_city = pickUpCity
                cInt.drop_city = dropCity

                mViewModel.callCreateIntent(cInt)

            }

        }

        mViewModel.cIResultLiveData.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data?.data!=null && it.data.data.size>0) {
                            shortToast(it.data.message!!)

                            it.data.data[0].let { ind ->

                                indentId = ind.id.toString()
                                prefs.save(AppConstant.IS_NEW_INDENT_CREATED,"YES")
                                prefs.save(AppConstant.CREATED_ENQ_NUMBER,indentId)
                                prefs.save(AppConstant.FROM_LOCATION,pickUpLoc)
                                prefs.save(AppConstant.TO_LOCATION,dropLoc)
                                prefs.save(AppConstant.INDENT_STATUS,ind.status.toString())
                                ind.reqDate?.let {
                                    prefs.save(AppConstant.DATE,it)
                                }

                                val weight = ind.weight + ind.weightUnit
                                prefs.save(AppConstant.WEIGHT,weight)

                                if (ind.materialTypeName!=null && !ind.materialTypeName.equals("Others",true)) {
                                    mBinding.valMatType.text = ind.materialTypeName + ", " +weight
                                }else{
                                    mBinding.valMatType.text = ind.newMaterialType + ", " +weight
                                }

                                var bodyType = ""
                                try {
                                    if (ind.bodyType.equals("Others",true)){
                                        bodyType = ind.newBodyType.toString()
                                    }else{
                                        bodyType = ind.bodyType.toString()
                                    }
                                }catch (e:Exception) {
                                    e.printStackTrace()
                                }

                                if (ind.truckTypeName!=null && !ind.truckTypeName.equals("Others",true)) {
                                    mBinding.valTruckType.text = ind.truckTypeName + ", "+bodyType
                                }else{
                                    mBinding.valTruckType.text = ind.newTruckType + ", "+bodyType
                                }

                                prefs.save(AppConstant.TRUCK_TYPE,mBinding.valTruckType.text.toString())
                                prefs.save(AppConstant.MAT_TYPE,mBinding.valMatType.text.toString())
                            }

                            waitingIndent("submit")
                        }else{
                            //shortToast("No Response")
                        }
                    }
                    Status.ERROR -> {
                        shortToast(it.message!!)
                    }
                }
            }
        })

        mBinding.etDate.setOnClickListener {
            callDatePicker(mBinding.etDate,AppConstant.CREATE_INDENT)
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        sDate = sdf.format(Date())
        mBinding.etDate.setText(sDate)

        viewModel.customerIndentLiveData.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data?.status == 200) {

                            if (it.data.data!=null && it.data.data.size>0) {

                                if (AppUtils.checkRate(it.data.data[0].customerRate)){
                                    Log.d("Customer Rate","rate Given" +it.data.data[0].customerRate)
                                    prefs.save(AppConstant.AMOUNT,it.data.data[0].customerRate.toString())
                                    mBinding.amt.text = it.data.data[0].customerRate
                                    mBinding.btnLay.visibility = View.VISIBLE
                                    mBinding.title.text = resources.getString(R.string.wait_for_confirmation)

                                    it.data.data[0].status?.let {status ->
                                        if (status.toInt()>=1) {
                                            afterWinSuccess()
                                        }
                                    }

                                    /*if (it.data.data[0].status.equals("1")) {
                                        afterWinSuccess()
                                    }else if (it.data.data[0].status.equals("1")) {
                                        afterCancel()
                                    }*/

                                    //waitingIndent()
                                }else {
                                    //Log.d("Customer Rate","No rate Given"+it.data.data[0].customerRate)
                                    Log.d("Customer Rate","No rate Given no")
                                }

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

        //data!!.customerRate

        viewModel.confirmTripLiveData.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    afterWinSuccess()
                                    clearFields()
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

        mBinding.btnConfirm.setOnClickListener {

            if (mBinding.title.text.toString() == resources.getString(R.string.wait_for_rate)){
                shortToast("Customer rate not given")
            }else {
                showProgressDialog()
                viewModel.callConfirmTrip(indentId,userId,role)
            }

        }

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
                                    afterCancel()
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
                            }/*else {
                                callCancelApi(cancelReason,"",edtDate.text.toString())
                            }*/
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

        mViewModel.bodyTypeData.observe(viewLifecycleOwner,Observer{

            if (it?.data!=null && it.status == 200) {
                val bodyTypeList = mutableListOf<String>()

                bodyTypeList.add("Select body type")

                for (body in it.data) {
                    bodyTypeList.add(body.name.toString())
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    val adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, bodyTypeList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mBinding.bodyTypeSpinner.adapter = adapter
                }
            }

        })

        mViewModel.truckTypeData.observe(viewLifecycleOwner,Observer {
            if (it?.data!=null && it.status == 200){
                val truckTypesList = ArrayList<SpinnerData>()

                try{

                    val selectType = SpinnerData()
                    selectType.id = 0
                    selectType.name = "Select truck type"

                    truckTypesList.add(selectType)
                    truckTypesList.addAll(it.data)

                    lifecycleScope.launch(Dispatchers.Main) {
                        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, truckTypesList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mBinding.truckTypeSpinner.adapter = adapter
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        })

        mViewModel.materialTypeData.observe(viewLifecycleOwner,Observer{
            if (it?.data!=null && it.status == 200){
                val materialTypesList = ArrayList<SpinnerData>()

                try{

                    val selectType = SpinnerData()
                    selectType.id = 0
                    selectType.name = "Select material type"

                    materialTypesList.add(selectType)
                    materialTypesList.addAll(it.data)

                    lifecycleScope.launch(Dispatchers.Main) {
                        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, materialTypesList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mBinding.materialTypeSpinner.adapter = adapter
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

        })

        return mBinding.root
    }

    private fun afterCancel() {
        try {
            clearIndentPref()
            setInitView()
            clearFields()
            shortToast("Indent Cancelled")
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun clearFields() {
        try {

            pickUpAutocompleteFragment.setText("")
            dropAutocompleteFragment.setText("")
            mBinding.txtFrom.text ="From"
            mBinding.txtTo.text ="To"
            pickUpLoc = ""
            dropLoc = ""
            mBinding.weightSpinner.setSelection(0)
            mBinding.etWeight.setText("")
            mBinding.truckTypeSpinner.setSelection(0)
            mBinding.materialTypeSpinner.setSelection(0)
            mBinding.bodyTypeSpinner.setSelection(0)

            mMap.clear()

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun afterWinSuccess() {
        cancelTimer()
        val data: Indents? = null
        callCustomerViewTripsDetails(data,"1","map",indentId)
        clearIndentPref()
        setInitView()
    }

    private fun cancelTimer() {
        try {
            myTimer?.cancel()
            myTimer = null
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun setInitView() {
        cancelTimer()
        val nIndent = prefs.getValueString(AppConstant.IS_NEW_INDENT_CREATED)
        if (nIndent.isNullOrEmpty()) {
            createIndent()
        }else {
            waitingIndent("res")
        }
    }

    private fun waitingIndent(posi:String) {
        mBinding.createIndentLay.visibility = View.GONE
        mBinding.waitRateLay.visibility = View.VISIBLE

        try {

            val from = prefs.getValueString(AppConstant.FROM_LOCATION)
            val to = prefs.getValueString(AppConstant.TO_LOCATION)
            val amount = prefs.getValueString(AppConstant.AMOUNT)
            indentId = prefs.getValueString(AppConstant.CREATED_ENQ_NUMBER).toString()

            if (!from.isNullOrEmpty()) {
                mBinding.loc.txtPick.text = from
            }
            if (!to.isNullOrEmpty()) {
                mBinding.loc.txtDrop.text = to
            }
            if (!amount.isNullOrEmpty()) {
                mBinding.amt.text = amount
                mBinding.btnLay.visibility = View.VISIBLE
                mBinding.title.text = resources.getString(R.string.wait_for_confirmation)

            }

            mBinding.valTruckType.text = prefs.getValueString(AppConstant.TRUCK_TYPE)
            mBinding.valMatType.text =  prefs.getValueString(AppConstant.MAT_TYPE)
            mBinding.loc.txtKm.text = prefs.getValueString(AppConstant.KM)
            mBinding.txtDate.text = prefs.getValueString(AppConstant.DATE)
            mBinding.txtEnq.text = "DT" + indentId

            try {

                if (posi.equals("res",true)){
                    fromLatLng = getLocationFromAddress(from)
                    toLatLng = getLocationFromAddress(to)

                    if (fromLatLng!=null && toLatLng!=null) {

                        originLatitude = fromLatLng!!.latitude
                        originLongitude = fromLatLng!!.longitude

                        destinationLatitude = toLatLng!!.latitude
                        destinationLongitude = toLatLng!!.longitude

                        Log.d("from",fromLatLng!!.latitude.toString() + " " +fromLatLng!!.longitude.toString())
                        Log.d("to",toLatLng!!.latitude.toString() + " " +toLatLng!!.longitude.toString())
                        onMapReady(mMap)
                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

        }catch (e:Exception){
            e.printStackTrace()
        }

        callIntentDetailsAPI()

    }

    private fun callIntentDetailsAPI() {

        val status = prefs.getValueString(AppConstant.INDENT_STATUS)

        if (indentId.isNotEmpty() &&
            (prefs.getValueString(AppConstant.AMOUNT).isNullOrEmpty() ||
            !status.isNullOrEmpty() && status.equals("0"))){

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
    }

    private fun createIndent() {
        mBinding.createIndentLay.visibility = View.VISIBLE
        mBinding.waitRateLay.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.getSpinnerData()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            if (originLatitude!=0.0 && destinationLatitude!=0.0) {
                mMap.clear()

                originLocation = LatLng(originLatitude, originLongitude)
                mMap.addMarker(MarkerOptions().position(originLocation))

                destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                mMap.addMarker(MarkerOptions().position(destinationLocation))

                val url = getDirectionURL(originLocation, destinationLocation, AppUtils.getMKStr())
                Log.d("url",url)
                GetDirection(url).execute()

                val latlngBuilder = LatLngBounds.Builder()

                latlngBuilder.include(originLocation)
                latlngBuilder.include(destinationLocation)

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 200))

                try{
                    //val results = FloatArray(1)
                    val dist = AppUtils.getDistance(originLocation,destinationLocation)
                    prefs.save(AppConstant.KM,dist)
                    mBinding.loc.txtKm.text = dist
                    
                }catch (e:Exception){
                    e.printStackTrace()
                }

                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun getLatLong(latlong: LatLng?) {

        latlong?.let {
            latlong.latitude;
            latlong.latitude;
        }

    }

    private fun callDatePicker(edtDate: EditText,from:String) {
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

    private fun fetchPlaceDetails(
        placeId: String, type: String) {

        try{

            val placesClient = Places.createClient(requireActivity())
            val request = FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS)).build()

            placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                if (type.equals(AppConstant.PICKUP_LOC,true)){
                    pickUpAutocompleteFragment.setText(place.address)
                    mBinding.txtFrom.text = place.address
                    pickUpLoc = place.address!!
                }else {
                    dropAutocompleteFragment.setText(place.address)
                    mBinding.txtTo.text = place.address
                    dropLoc = place.address!!
                }

            }.addOnFailureListener { exception: Exception ->

            }

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun getCityName(latLng: LatLng?, type: String) {

        latLng?.let {

            try {

                var mGeocoder = Geocoder(requireActivity(), Locale.getDefault())

                val addresses: MutableList<Address>? = mGeocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses != null && addresses.size > 0) {

                    if (type.equals(AppConstant.PICKUP_LOC,true)){
                        pickUpCity = addresses[0].locality
                        originLatitude = it.latitude
                        originLongitude = it.longitude
                    }else {
                        dropCity = addresses[0].locality
                        destinationLatitude = it.latitude
                        destinationLongitude = it.longitude
                    }

                    try{
                        if (originLatitude!=0.0) {
                            originLocation = LatLng(originLatitude, originLongitude)
                            mMap.addMarker(MarkerOptions().position(originLocation))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 12F))
                        }

                        if (destinationLatitude!=0.0) {
                            destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                            mMap.addMarker(MarkerOptions().position(destinationLocation))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 12F))
                        }


                        if (originLatitude!=0.0 && destinationLatitude!=0.0) {
                            onMapReady(mMap)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }

            }catch (e:Exception){
                e.printStackTrace()
            }

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

    private fun callCancelApi(cancelReason: String, reason: String, date: String,fReason:String) {
        showProgressDialog()
        viewModel.callCancelTrip(indentId,userId,cancelReason,reason,date,role,fReason)
    }

    fun clearIndentPref() {
        prefs.deleteKey(AppConstant.FROM_LOCATION)
        prefs.deleteKey(AppConstant.TO_LOCATION)
        prefs.deleteKey(AppConstant.AMOUNT)
        prefs.deleteKey(AppConstant.IS_NEW_INDENT_CREATED)
        prefs.deleteKey(AppConstant.CREATED_ENQ_NUMBER)
        prefs.deleteKey(AppConstant.MAT_TYPE)
        prefs.deleteKey(AppConstant.TRUCK_TYPE)
        prefs.deleteKey(AppConstant.WEIGHT)
        prefs.deleteKey(AppConstant.DATE)
        prefs.deleteKey(AppConstant.INDENT_STATUS)
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
        Log.d("ACTIVITY","onDestroy")
    }

    override fun onResume() {
        super.onResume()
        setInitView()
        //Log.d("",AppUtils.getMKStr())
    }

    override fun onPause() {
        super.onPause()
        Log.d("ACTIVITY","onPause")
    }

    override fun onStop() {
        super.onStop()
        cancelTimer()
        Log.d("ACTIVITY","onStop")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ACTIVITY","onDestroyView")
    }

    fun getLocationFromAddress(strAddress: String?): LatLng? {

        try {
            val coder = Geocoder(requireActivity())
            val address: List<Address>?
            var p1: LatLng? = null

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

    fun processData(
        bodyTypes: TypesPojo?,
        truckTypes: TypesPojo?,
        materialTypes: TypesPojo?,
    ) {

        dismissProgressDialog()

        val bodyTypeList = mutableListOf<String>()
        val truckTypesList = ArrayList<SpinnerData>()
        val materialTypesList = ArrayList<SpinnerData>()

        lifecycleScope.launch(Dispatchers.Default) {

            if (bodyTypes?.data != null && bodyTypes.status == 200) {

                //try{

                bodyTypeList.add("Select body type")

                for (body in bodyTypes.data) {
                    bodyTypeList.add(body.name.toString())
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    val adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, bodyTypeList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mBinding.bodyTypeSpinner.adapter = adapter
                }

                /* }catch (e:Exception){
                     e.printStackTrace()
                 }*/

            }

            if (truckTypes?.data!=null && truckTypes.status == 200) {

                try{

                    val selectType = SpinnerData()
                    selectType.id = 0
                    selectType.name = "Select truck type"

                    truckTypesList.add(selectType)
                    truckTypesList.addAll(truckTypes.data)

                    lifecycleScope.launch(Dispatchers.Main) {
                        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, truckTypesList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mBinding.truckTypeSpinner.adapter = adapter
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            if (materialTypes?.data!=null && materialTypes.status == 200) {

                try{

                    val selectType = SpinnerData()
                    selectType.id = 0
                    selectType.name = "Select material type"

                    materialTypesList.add(selectType)
                    materialTypesList.addAll(materialTypes.data)

                    lifecycleScope.launch(Dispatchers.Main) {
                        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, materialTypesList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mBinding.materialTypeSpinner.adapter = adapter
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

        }

    }


}