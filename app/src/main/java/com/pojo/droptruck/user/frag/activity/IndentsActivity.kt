package com.pojo.droptruck.activity.indents

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivityIndentsBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IndentsActivity : BaseFragment() {

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

    val st1 = "UVVsNllWTjVRakpzY1Z"
    val st2 = "aSmVGQTNjVkJUVTAxdW"
    val st3 = "IwVnFWVzV4TXpCNFY"
    val st4 = "ybFJRMDkxTFVSego="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!Places.isInitialized()){
            context?.let { context ->
                Places.initialize(context, getSplitedDecodedStr().toString())
            }

        }
        val pickUpAutocompleteFragment = (childFragmentManager.findFragmentById(R.id.pickupAutocomplete)
                as AutocompleteSupportFragment)
        pickUpAutocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )

        val dropAutocompleteFragment = (childFragmentManager.findFragmentById(R.id.dropAutocomplete)
                as AutocompleteSupportFragment)
        dropAutocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )

        pickUpAutocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: com.google.android.gms.common.api.Status) {
                println("ErrorReceived")
                println(p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {

                getLatLong(p0.latLng);
                println("GotPlaces " +p0.name)
                pickUpLoc = p0.name.toString()
            }

        })


        dropAutocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: com.google.android.gms.common.api.Status) {
                println("ErrorReceived")
                println(p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {

                getLatLong(p0.latLng);
                println("GotPlaces "+p0.name)
                dropLoc = p0.name.toString()
            }

        })

    }

    private fun getLatLong(latlong: LatLng?) {

        latlong?.let {
            latlong.latitude;
            latlong.latitude;
        }



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mBinding = ActivityIndentsBinding.inflate(inflater, container, false)
        mBinding.indent = mViewModel
        mBinding.lifecycleOwner = this

        val languages = resources.getStringArray(R.array.Languages)

        mBinding.toolbar.titleTxt.text = resources.getString(R.string.create_intent)






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


        mBinding.pickupSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                var pos = position +1
                //pickUpLoc = pos.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mBinding.dropSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                var pos = position +1
                //dropLoc = pos.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
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
                var pos = position +1
                truckType = pos.toString()

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

                if (position>0) {
                    matType = pos.toString()
                }else{
                    matType = "" +
                            ""
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
            }else if (mBinding.truckTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Truck Type")
            }else if (mBinding.bodyTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Body Type")
            }*/else if (mBinding.etWeight.text.toString().trim().isEmpty()){
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

                cInt.customerName = mBinding.etCustomer.text.toString().trim()
                cInt.companyName = mBinding.etCompany.text.toString().trim()
                cInt.numberOne = mBinding.etNum1.text.toString().trim()
                cInt.numberTwo = mBinding.etNum2.text.toString().trim()
                cInt.sourceLead = sourceOfLead
                cInt.pickUpLocation = pickUpLoc
                cInt.dropLocation = dropLoc
                cInt.truckType = truckType
                cInt.bodyType = bodyType
                cInt.weight = mBinding.etWeight.text.toString().trim()
                cInt.weightUnit = weightUnit
                cInt.materialType = matType

                cInt.podSoft = pod
                cInt.remarks = mBinding.etRemarks.text.toString().trim()

                cInt.payTerms = payType

                mViewModel.callCreateIntent(cInt)

            }

        }

        mViewModel.resultLiveData.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data!=null) {
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
        })

        return mBinding.root
    }


    fun getSplitedDecodedStr(): String? {
        return String(
            Base64.decode(
                Base64.decode(
                    st1 +
                            st2 +
                            st3 +
                            st4,
                    Base64.DEFAULT
                ),
                Base64.DEFAULT
            )
        )
    }

}