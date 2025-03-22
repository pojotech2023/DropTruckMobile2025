package com.pojo.droptruck.user.act

import android.app.DatePickerDialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivityCustomerIndentBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.user.model.UserCreateIndent
import com.pojo.droptruck.user.viewmodel.UserIndentsViewModel
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CustomerIndentActivity : BaseActivity() {
    lateinit var mBinding: ActivityCustomerIndentBinding
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
    var sDate = ""

    var newMatType = ""
    var newTruckType = ""
    var newBodyType = ""
    var newSourceType = ""

    lateinit var pickUpAutocompleteFragment: AutocompleteSupportFragment
    lateinit var dropAutocompleteFragment: AutocompleteSupportFragment

    var pickUpCity = ""
    var dropCity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCustomerIndentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val languages = resources.getStringArray(R.array.Languages)

        mBinding.toolbar.titleTxt.text = resources.getString(R.string.create_intent)

        try {
            if (intent!=null) {

                data = (intent.getParcelableExtra("data") as? Indents)!!
                Log.d("Data: ", Gson().toJson(data))

                if (::data.isInitialized) {
                    mBinding.etCustomer.setText(data.customerName)
                    mBinding.etCompany.setText(data.companyName)
                    mBinding.etNum1.setText(data.number1)

                    data.number2?.let {
                        mBinding.etNum2.setText(it)
                    }

                    mBinding.etWeight.setText(data.weight)
                    data.remarks?.let {
                        mBinding.etRemarks.setText(it)
                    }

                    setSpinnerData()

                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        if(!Places.isInitialized()){
            //context?.let { context ->
            Places.initialize(applicationContext, AppUtils.getMKStr())
            //}

        }
        //val pickUpAutocompleteFragment = (childFragmentManager.findFragmentById(R.id.pickupAutocomplete)
        pickUpAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.pickupAutocomplete) as AutocompleteSupportFragment

        pickUpAutocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG,Place.Field.ID,
            Place.Field.NAME,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS))

        //val dropAutocompleteFragment = (childFragmentManager.findFragmentById(R.id.dropAutocomplete)
        dropAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.dropAutocomplete) as AutocompleteSupportFragment
        dropAutocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME,
            Place.Field.ADDRESS,Place.Field.ID,Place.Field.ADDRESS_COMPONENTS))


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


        mViewModel.showToast.observe(this, Observer {
            if (it!=null){
                shortToast(it)
            }
        })

        mViewModel.mLoader.observe(this, Observer {
            if (it!=null) {
                if (it){
                    showProgressDialog()
                    shortToast("Success")
                }else {
                    dismissProgressDialog()
                }
            }
        })

        mBinding.sourceLeadSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)

                if(position>0) {
                    sourceOfLead = parent.selectedItem.toString()
                }else {
                    sourceOfLead = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        mBinding.truckTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                truckType = position.toString()

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

                matType = position.toString()

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

        mBinding.paymentSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                var pos = position +1
                payType = if (position>0) {
                    parent.selectedItem.toString()
                }else {
                    ""
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mBinding.podSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                var pos = position +1
                pod = if (position>0) {
                    parent.selectedItem.toString()
                }else {
                    ""
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

        mViewModel.cIResultLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data!=null) {
                            shortToast(it.data.message!!)
                            finish()
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

        mBinding.imgBack.setOnClickListener { finish() }

        mBinding.etDate.setOnClickListener {
            callDatePicker(mBinding.etDate)
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
        sDate = sdf.format(Date())
        mBinding.etDate.setText(sDate)

    }

    private fun setSpinnerData() {

        try {
            mBinding.payTermsLay.visibility = View.GONE
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {

            mBinding.sourceLeadSpinner.setSelection((mBinding.sourceLeadSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.sourceOfLead.toString()))

            mBinding.truckTypeSpinner.setSelection((mBinding.truckTypeSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.truckTypeId.toString()))

            mBinding.bodyTypeSpinner.setSelection((mBinding.bodyTypeSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.bodyType.toString()))

            mBinding.weightSpinner.setSelection((mBinding.weightSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.weightUnit.toString()))

            /*mBinding.materialTypeSpinner.setSelection((mBinding.materialTypeSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.materialTypeId.toString()))*/

            mBinding.materialTypeSpinner.setSelection(data.materialTypeId!!.toInt())

            /* mBinding.paymentSpinner.setSelection((mBinding.paymentSpinner.getAdapter() as ArrayAdapter<String>)
                 .getPosition(data.paymentTerms.toString()))*/

            mBinding.podSpinner.setSelection((mBinding.podSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.podSoftHardCopy.toString()))

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

    private fun callDatePicker(edtDate: EditText) {
        val calendar = Calendar.getInstance()
        val mYear: Int = calendar.get(Calendar.YEAR) // current year
        val mMonth: Int = calendar.get(Calendar.MONTH) // current month
        val mDay: Int = calendar.get(Calendar.DAY_OF_MONTH) // current day

        val dialog = DatePickerDialog(this@CustomerIndentActivity, { _, year, month, day_of_month ->

            val cMonth = month + 1
            val date = "$day_of_month-$cMonth-$year"
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

            val placesClient = Places.createClient(this)
            val request = FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS)).build()

            placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                if (type.equals(AppConstant.PICKUP_LOC,true)){
                    pickUpAutocompleteFragment.setText(place.address)
                    pickUpLoc = place.address!!
                }else {
                    dropAutocompleteFragment.setText(place.address)
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

                var mGeocoder = Geocoder(this@CustomerIndentActivity, Locale.getDefault())

                val addresses: MutableList<Address>? = mGeocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses != null && addresses.size > 0) {

                    if (type.equals(AppConstant.PICKUP_LOC,true)){
                        pickUpCity = addresses[0].locality
                    }else {
                        dropCity = addresses[0].locality
                    }

                }

            }catch (e:Exception){
                e.printStackTrace()
            }

        }

    }

}