package com.pojo.droptruck.activity.indents

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivityIndentsBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.pojo.SpinnerData
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Arrays
import java.util.Locale


@AndroidEntryPoint
class IndentActivity : BaseActivity() {

    lateinit var mBinding: ActivityIndentsBinding
    val mViewModel: IndentsViewModel by viewModels()

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

    var newMatType = ""
    var newTruckType = ""
    var newBodyType = ""
    var newSourceType = ""

    lateinit var pickUpAutocompleteFragment: AutocompleteSupportFragment
    lateinit var dropAutocompleteFragment: AutocompleteSupportFragment

    var pickUpCity = ""
    var dropCity = ""

    lateinit var mTruckAdapter: ArrayAdapter<SpinnerData>
    lateinit var mMaterialAdapter: ArrayAdapter<SpinnerData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityIndentsBinding.inflate(layoutInflater)
        mBinding.indent = mViewModel
        mBinding.lifecycleOwner = this
        setContentView(mBinding.root)

        mBinding.toolbar.titleTxt.text = resources.getString(R.string.create_intent)

        /*lifecycleScope.launch(Dispatchers.IO) {
            mViewModel.getSpinnerData(this@IndentActivity)
        }*/

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

                    try {

                        data.newBodyType?.let {
                            mBinding.bodyTypeLay.visibility = View.VISIBLE
                            mBinding.etBodyType.setText(it)
                        }

                        data.newTruckType?.let {
                            mBinding.truckLay.visibility = View.VISIBLE
                            mBinding.etTruckType.setText(it)
                        }

                        data.newSourceType?.let {
                            mBinding.sourceOfLeadLay.visibility = View.VISIBLE
                            mBinding.etSourceOfLead.setText(it)
                        }

                        data.newMaterialType?.let {
                            mBinding.matLay.visibility = View.VISIBLE
                            mBinding.etMatType.setText(it)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                    setSpinnerData()

                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        showProgressDialog()
        mViewModel.getSpinnerData(this@IndentActivity)

        try{

        if(!Places.isInitialized()){
            //context?.let { context ->
                Places.initialize(applicationContext, AppUtils.getMKStr())
            //}

        }

        pickUpAutocompleteFragment = supportFragmentManager.findFragmentById(R.id.pickupAutocomplete) as AutocompleteSupportFragment

        pickUpAutocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG,
            Place.Field.NAME,Place.Field.ADDRESS,Place.Field.ID,Place.Field.ADDRESS_COMPONENTS))

        pickUpAutocompleteFragment.view?.post {
            println("InsideLocationView")
            pickUpAutocompleteFragment.setHint("Enter your location")
        }

        pickUpAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: com.google.android.gms.common.api.Status) {
                println("ErrorReceived")
                println(p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {

                getLatLong(p0.latLng);
                println("GotPlaces " +p0.address)
                //pickUpLoc = p0.name.toString()
                pickUpLoc = p0.address
                fetchPlaceDetails(p0.id,AppConstant.PICKUP_LOC)
                getCityName(p0.latLng,AppConstant.PICKUP_LOC)
                //pickUpAutocompleteFragment.setText(pickUpLoc)
            }

        })

        //val dropAutocompleteFragment = (childFragmentManager.findFragmentById(R.id.dropAutocomplete)
        dropAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.dropAutocomplete) as AutocompleteSupportFragment
        dropAutocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME,
            Place.Field.ADDRESS,Place.Field.ID,Place.Field.ADDRESS_COMPONENTS))


        dropAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: com.google.android.gms.common.api.Status) {
                println("ErrorReceived")
                println(p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {

                getLatLong(p0.latLng)
                println("GotPlaces "+p0.address)
                //dropLoc = p0.name.toString()
                dropLoc = p0.address

                fetchPlaceDetails(p0.id,AppConstant.DROP_LOC)
                getCityName(p0.latLng,AppConstant.DROP_LOC)
            }

        })

        }catch (e:Exception){
            e.printStackTrace()
        }


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


        try{

        mBinding.sourceLeadSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)

                /*if(position>0) {
                    sourceOfLead = parent.selectedItem.toString()
                }else {
                    sourceOfLead = ""
                }*/

                sourceOfLead = parent.selectedItem.toString()
                if (position>0 && !parent.selectedItem.toString().equals("Others",true)) {
                    newSourceType = ""
                    mBinding.sourceOfLeadLay.visibility = View.GONE
                }else if (parent.selectedItem.toString().equals("Others",true)){
                    mBinding.sourceOfLeadLay.visibility = View.VISIBLE
                }else{
                    sourceOfLead = ""
                    newSourceType = ""
                    mBinding.sourceOfLeadLay.visibility = View.GONE
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
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position + " " + id)

                //truckType = position.toString() //old...
                try{
                    val selectedObject = mBinding.truckTypeSpinner.selectedItem as SpinnerData
                    truckType = selectedObject.id.toString() //new...
                }catch (e:Exception){
                    truckType = position.toString() //old...
                    e.printStackTrace()
                }

                Log.d("Item: ", "selected item truckType" + truckType)

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
                Log.d("Item: ", "selected item bodyType" + bodyType)

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
                try{
                    Log.d("materialTypeSpinner: ", "selected item " + parent.selectedItem + " " + position)

                    //matType = position.toString() //old...
                    try{
                        val selectedObject = mBinding.materialTypeSpinner.selectedItem as SpinnerData
                        matType = selectedObject.id.toString() //new...
                    }catch (e:Exception){
                        matType = position.toString() //old...
                        e.printStackTrace()
                    }

                    Log.d("materialTypeSpinner: ", "selected item matType" + matType)

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
                }catch (e:Exception){
                    e.printStackTrace()
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

        }catch (e:Exception){
            e.printStackTrace()
        }



        mBinding.btnSubmit.setOnClickListener {

            if (mBinding.etCustomer.text.toString().trim().isEmpty()) {
                shortToast("Enter Customer Name")
            }/*else if (mBinding.etCompany.text.toString().trim().isEmpty()){
                shortToast("Enter Company Name")
            }*/else if (mBinding.etNum1.text.toString().trim().isEmpty()){
                shortToast("Enter Number one")
            }else if (mBinding.etNum1.text.toString().length !=10){
                shortToast("Number one must be ten digits")
            }/*else if (mBinding.etNum2.text.toString().trim().isEmpty()){
                shortToast("Enter Number Two")
            }*/else if (mBinding.etNum2.text.toString().trim().isNotEmpty() && mBinding.etNum2.text.toString().length !=10){
                shortToast("Number Two must be ten digits")
            }/*else if (mBinding.sourceLeadSpinner.selectedItemPosition == 0){
                shortToast("Select Source Lead")
            }else if (mBinding.pickupSpinner.selectedItemPosition == 0){
                shortToast("Select Pickup Location")
            }else if (mBinding.dropSpinner.selectedItemPosition == 0){
                shortToast("Select Drop Location")
            }*/else if (mBinding.truckTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Truck Type")
            }else if (mBinding.truckTypeSpinner.selectedItem.toString().equals("Others",true)
                && mBinding.etTruckType.text.toString().trim().isEmpty()){
                shortToast("Please Enter truck type")
            } else if (mBinding.bodyTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Body Type")
            }else if (mBinding.bodyTypeSpinner.selectedItem.toString().equals("Others",true)
                && mBinding.etBodyType.text.toString().trim().isEmpty()){
                shortToast("Please Enter body type")
            }else if (mBinding.etWeight.text.toString().trim().isEmpty()){
                shortToast("Enter Weight")
            }/*else if (mBinding.weightSpinner.selectedItemPosition == 0){
                shortToast("Select Weight")
            }else if (mBinding.materialTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Material Type")
            }else if (mBinding.etRemarks.text.toString().trim().isEmpty()) {
                shortToast("Enter Remarks")
            }*/else {

                showProgressDialog()
                val cInt = CreateIndentPOJO()

                val userId = prefs.getValueString(AppConstant.USER_ID)
                val role = prefs.getValueString(AppConstant.ROLE_ID)

                try {
                    if (::data.isInitialized){
                        cInt.indentId = data.id.toString()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                cInt.userId = userId.toString()
                cInt.userType = role.toString()
                cInt.customerName = mBinding.etCustomer.text.toString().trim()
                cInt.companyName = mBinding.etCompany.text.toString().trim()
                cInt.numberOne = mBinding.etNum1.text.toString().trim()
                cInt.numberTwo = mBinding.etNum2.text.toString().trim()
                cInt.sourceLead = sourceOfLead
                cInt.pickUpLocation = pickUpLoc
                cInt.dropLocation = dropLoc
                cInt.truckType = truckType
                cInt.bodyType = bodyType
                cInt.materialType = matType
                cInt.weight = mBinding.etWeight.text.toString().trim()
                cInt.weightUnit = weightUnit

                newBodyType = mBinding.etBodyType.text.toString().trim()
                newTruckType = mBinding.etTruckType.text.toString().trim()

                if (mBinding.etSourceOfLead.text.toString().trim().isNotEmpty()) {
                    newSourceType = mBinding.etSourceOfLead.text.toString().trim()
                }else {
                    newSourceType = ""
                }

                if (mBinding.etMatType.text.toString().trim().isNotEmpty()) {
                    newMatType = mBinding.etMatType.text.toString().trim()
                }else {
                    newMatType = ""
                }

                cInt.new_body_type = newBodyType
                cInt.new_truck_type = newTruckType
                cInt.new_source_type = newSourceType
                cInt.new_material_type = newMatType

                /*if (matType.equals("Others",true)) {
                    if (mBinding.etMatType.text.toString().trim().isNotEmpty()) {
                        cInt.materialType = mBinding.etMatType.text.toString().trim()
                    }else {
                        cInt.materialType = ""
                    }
                }else{
                    cInt.materialType = matType
                }*/

                cInt.podSoft = pod
                cInt.remarks = mBinding.etRemarks.text.toString().trim()

                cInt.payTerms = payType

                cInt.pickup_city = pickUpCity
                cInt.drop_city = dropCity

                mViewModel.callCreateIntent(cInt)

            }

        }

        mViewModel.resultLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data!=null) {
                            //shortToast(it.data.message!!)
                            shortToast("Indent insert successfully")
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

        mBinding.etNum1.addTextChangedListener(textWatcher)

        mViewModel.customerDtlLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            if (it.data.status == 200) {

                                try {

                                    mBinding.etCustomer.setText(it.data.data?.customerName)
                                    mBinding.etCompany.setText(it.data.data?.companyName)

                                }catch (e:Exception){
                                    e.printStackTrace()
                                }

                            }else {
                                shortToast(it.data.message!!)
                            }
                        }
                    }
                    Status.ERROR -> {
                        shortToast(it.message!!)
                    }
                }
            }
        })

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
                    val adapter = ArrayAdapter<String>(this@IndentActivity, android.R.layout.simple_spinner_item, bodyTypeList)
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
                        mTruckAdapter = ArrayAdapter(this@IndentActivity, android.R.layout.simple_spinner_item, truckTypesList)
                        mTruckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mBinding.truckTypeSpinner.adapter = mTruckAdapter
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
                        mMaterialAdapter = ArrayAdapter(this@IndentActivity, android.R.layout.simple_spinner_item, materialTypesList)
                        mMaterialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mBinding.materialTypeSpinner.adapter = mMaterialAdapter
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
            Log.d("TAG_INDENT_ACT", "res: ")
            delay(500)
            Log.d("TAG_INDENT_ACT", "after delay: ")
            if (::data.isInitialized) {
                lifecycleScope.launch(Dispatchers.Main) {

                    setSpinnerData()
                }
            }

        }

    }

    private fun getCityName(latLng: LatLng?, type: String) {

        latLng?.let {

            try {

                var mGeocoder = Geocoder(this@IndentActivity, Locale.getDefault())

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

    private fun setSpinnerData() {
        Log.d("TAG_INDENT_ACT", "setSpinnerData: ")
        try {
            mBinding.payTermsLay.visibility = View.GONE
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {

            mBinding.sourceLeadSpinner.setSelection((mBinding.sourceLeadSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.sourceOfLead.toString()))

            setSelectedItem(mBinding.truckTypeSpinner,mTruckAdapter,data.truckTypeName)
            setSelectedItem(mBinding.materialTypeSpinner,mMaterialAdapter,data.materialTypeName)

            /*mBinding.truckTypeSpinner.setSelection((mBinding.truckTypeSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.truckTypeName.toString()))*/

            mBinding.bodyTypeSpinner.setSelection((mBinding.bodyTypeSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.bodyType.toString()))

            mBinding.weightSpinner.setSelection((mBinding.weightSpinner.getAdapter() as ArrayAdapter<String>)
                .getPosition(data.weightUnit.toString()))

            /*try{

                val count = mBinding.materialTypeSpinner.adapter?.count

                data.materialTypeId?.let {

                    if (count != null) {
                        if (count > it.toInt()) {
                            Log.d("TAG", "setSpinnerData: "+count)
                            mBinding.materialTypeSpinner.setSelection(data.materialTypeId!!.toInt())
                        }
                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
            }*/

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

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (s.toString().isNotEmpty()) {
                    if (s.toString().length ==10) {
                        showProgressDialog()
                        mViewModel.callGetCustomerDetails(s.toString())
                    }
                }
            }catch (e:Exception){

                e.printStackTrace()
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun setSelectedItem(spinner: Spinner, adapter: ArrayAdapter<SpinnerData>, value: String?) {
        try{
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i).toString().equals(value)) {
                    spinner.setSelection(i)
                    break
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}