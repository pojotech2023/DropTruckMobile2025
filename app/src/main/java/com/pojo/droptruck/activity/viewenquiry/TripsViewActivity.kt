package com.pojo.droptruck.activity.viewenquiry

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivityTripsViewBinding
import com.pojo.droptruck.pojo.DriverDetail
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callImageViewActivity
import com.pojo.droptruck.utils.callWebView
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsViewActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityTripsViewBinding
    val mViewModel: ViewEnquiryViewModel by viewModels()

    var userId: String = ""

    private var vehiclePhoto = ""
    private var driverLicense = ""
    private var rcBook = ""
    var insurance = ""
    private var panCard = ""
    private var businessCard = ""
    private var invoice = ""
    private var otherDoc = ""
    private var bankDtl = ""
    private var memo = ""
    private var eBill = ""

    var data: Indents? = Indents()
    var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTripsViewBinding.inflate(layoutInflater)
        mBinding.obj = mViewModel
        mBinding.lifecycleOwner = this
        setContentView(mBinding.root)

        val role = prefs.getValueString(AppConstant.ROLE_ID)
        userId = prefs.getValueString(AppConstant.USER_ID)!!

        prefs.save(AppConstant.VIEW_INDENT,"VIEW_INDENT")

        mBinding.imgLay.setOnClickListener { finish() }

        if (role.equals(AppConstant.SUPPLIER)) {
            //mBinding.notSupplier.visibility = View.GONE

            mBinding.titleCustomerRate.visibility = View.GONE
            mBinding.txtRate.visibility = View.GONE

        }else if (role.equals(AppConstant.SALES)) {
            mBinding.titleCustomerRate.visibility = View.VISIBLE
            mBinding.txtRate.visibility = View.VISIBLE
        }

        try {
            if (intent!=null){
                data = (intent.getParcelableExtra("data") as? Indents)
                status = intent.getStringExtra("status").toString()
                if (data!=null){
                    mBinding.txtEnq.text = "DT"+data?.id.toString();
                    mViewModel.setIndents(data!!)

                    /*if (role.equals(AppConstant.SUPPLIER)
                        || role.equals(AppConstant.SALES)) {*/

                        if (data?.driverDetails?.size!! >0) {
                            data?.driverDetails?.get(0)?.let { setDriverDetails(it) }
                        }

                        if (role.equals(AppConstant.SUPPLIER)) {
                                setVendorDetails(data)
                        }

                        when(data!!.status) {

                           /* AppConstant.TRIPS_CONFIRMED -> {
                                mViewModel.callDriverDetailsAPI(userId,data!!.id.toString())
                            }*/
                            AppConstant.TRIPS_LOADING -> {
                                mBinding.driverLay.visibility = View.VISIBLE
                                mViewModel.callDriverDetailsAPI(userId,data!!.id.toString())
                            }
                            AppConstant.TRIPS_COMPLETED ,AppConstant.TRIPS_ONTHEROAD,
                            AppConstant.TRIPS_UNLOADING, AppConstant.TRIPS_POD -> {
                                mBinding.vendorDtlLay.visibility = View.VISIBLE
                                mBinding.driverLay.visibility = View.VISIBLE
                                mViewModel.callDriverDetailsAPI(userId,data!!.indentId.toString())
                            }
                            /*AppConstant.TRIPS_CONFIRMED -> {
                                mViewModel.callDriverDetailsAPI(userId,data!!.indentId.toString())
                            }*/

                            else -> {

                                if (AppUtils.checkStrEmpty(status)) {
                                    mBinding.vendorDtlLay.visibility = View.VISIBLE
                                    mBinding.driverLay.visibility = View.VISIBLE
                                    mViewModel.callDriverDetailsAPI(userId,data!!.id.toString())
                                }else {
                                    mBinding.vendorDtlLay.visibility = View.GONE
                                    mBinding.driverLay.visibility = View.GONE
                                }
                            }
                        }

                    //}


                    try {

                        mBinding.txtRate.text = data!!.customerRate

                        if (data?.materialTypeName.equals("Others",true)) {
                            mBinding.valMatType.text = data?.newMaterialType
                        }else{
                            mBinding.valMatType.text = data?.materialTypeName
                        }

                        /*if (data.indentRate!=null && data.indentRate.isNotEmpty()) {

                            if (role.equals(AppConstant.SUPPLIER)) {

                                val quotedAmountList = AppUtils.showSupplierQuotedAmounts(data.indentRate,userId)
                                adapter = AlgorithmAdapter(this, quotedAmountList, AppConstant.EnquiryViewActivity)

                            }
                            //else if (role.equals(AppConstant.SALES)) {
                            else {
                                adapter = AlgorithmAdapter(this, data.indentRate, AppConstant.EnquiryViewActivity)
                            }

                            mBinding.driverRateSpinner.adapter = adapter
                        }*/
                    }catch (e:Exception) {
                        e.printStackTrace()
                    }

                    if (role.equals(AppConstant.SALES) ||
                        role.equals(AppConstant.CUSTOMER)) {
                        mBinding.vendorDtlLay.visibility = View.GONE
                    }

                    try {
                        addExtraCost(data!!)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        mViewModel.resultLiveData.observe(this,Observer{
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data?.data != null  && it.data.data?.driver!=null) {

                            setDriverDetails(it.data.data?.driver!!)

                        }
                    }
                    Status.ERROR -> {
                        //shortToast(it.message.toString())
                    }
                }
            }
        })

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

        mBinding.trackLinkLayout.setOnClickListener {

            if(data!=null && data?.trackingLink!=null && data?.trackingLink?.isNotEmpty()!!) {
                callWebView(data?.trackingLink.toString())
            }else {
                shortToast("No Tracking Link Found")
            }
        }

        mBinding.panLayout.setOnClickListener {
            callImageViewActivity(panCard,resources.getString(R.string.pan),data?.id.toString())
        }
        mBinding.ewayLayout.setOnClickListener {
            callImageViewActivity(eBill,resources.getString(R.string.e_bill),data?.id.toString())
        }
        mBinding.inoviceLayout.setOnClickListener {
            callImageViewActivity(invoice,resources.getString(R.string.trips_invoice),data?.id.toString())
        }
        mBinding.bcardLayout.setOnClickListener {
            callImageViewActivity(businessCard,resources.getString(R.string.bCard),data?.id.toString())
        }
        mBinding.docLayout.setOnClickListener {
            callImageViewActivity(otherDoc,resources.getString(R.string.other_doc),data?.id.toString())
        }
        mBinding.bankDtlLayout.setOnClickListener {
            callImageViewActivity(bankDtl,resources.getString(R.string.bank_dtl),data?.id.toString())
        }
        mBinding.otherLayout.setOnClickListener {
            callImageViewActivity(memo,resources.getString(R.string.Others),data?.id.toString())
        }

    }

    private fun setDriverDetails(driver: DriverDetail) {

        driver.let {

            try {
                mBinding.valDriverName.text = it.driverName
                mBinding.valDriverNum.text = it.driverNumber
                mBinding.valVehicleNum.text = it.vehicleNumber
            }catch (e:Exception){
                e.printStackTrace()
            }

            try {

                var images = it

                images.let {

                    mBinding.imgLayout.visibility = View.VISIBLE

                    vehiclePhoto = AppUtils.IMAGE_BASE_URL+it.vehiclePhoto
                    driverLicense = AppUtils.IMAGE_BASE_URL+it.driverLicense
                    rcBook = AppUtils.IMAGE_BASE_URL+it.rcBook
                    insurance = AppUtils.IMAGE_BASE_URL+it.insurance

                    /*Glide.with(this@TripsViewActivity)
                        .load(AppUtils.IMAGE_BASE_URL+it.vehiclePhoto)
                        .error(R.drawable.identity_placeholder)
                        .into(mBinding.imgVehicle)

                    Glide.with(this@TripsViewActivity)
                        .load(AppUtils.IMAGE_BASE_URL+it.driverLicense)
                        .error(R.drawable.identity_placeholder)
                        .into(mBinding.imgDl)

                    Glide.with(this@TripsViewActivity)
                        .load(AppUtils.IMAGE_BASE_URL+it.rcBook)
                        .error(R.drawable.identity_placeholder)
                        .into(mBinding.imgRcb)

                    Glide.with(this@TripsViewActivity)
                        .load(AppUtils.IMAGE_BASE_URL+it.insurance)
                        .error(R.drawable.identity_placeholder)
                        .into(mBinding.imgIns)*/

                }

            }catch (e:Exception){
                e.printStackTrace()
            }

            try {

            }catch (e:Exception){
                e.printStackTrace()
            }

        }

    }


    private fun setVendorDetails(data: Indents?) {
        try {

            if (data?.suppliers!=null && data.suppliers!!.size>0) {

                data.suppliers!![0].let { vDtl ->

                    vDtl.supplierName?.let { mBinding.valVendorName.text = it }
                    vDtl.supplierType?.let { mBinding.valVendorType.text = it }
                    vDtl.contactNumber?.let { mBinding.valVendorNum.text = it }
                    vDtl.panCardNumber?.let { mBinding.valPanNum.text = it }
                    vDtl.bankName?.let { mBinding.valBankName.text = it }
                    vDtl.ifscCode?.let { mBinding.valIfscCode.text = it }
                    vDtl.accountNumber?.let { mBinding.valAccNum.text = it }

                    vDtl.panCard?.let { panCard = AppUtils.IMAGE_BASE_URL+it }
                    vDtl.businessCard?.let { businessCard = AppUtils.IMAGE_BASE_URL+it }
                    vDtl.ewayBill?.let { eBill = AppUtils.IMAGE_BASE_URL+it }
                    vDtl.tripsInvoices?.let { invoice = AppUtils.IMAGE_BASE_URL+it }
                    vDtl.otherDocument?.let { otherDoc = AppUtils.IMAGE_BASE_URL+it }
                    vDtl.bankDetails?.let { bankDtl = AppUtils.IMAGE_BASE_URL+it }
                    vDtl.memo?.let { memo = AppUtils.IMAGE_BASE_URL+it }

                }

            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun addExtraCost(indents: Indents):Double {
        var extraAmount = 0.0
        try{

            if (indents.extraCosts!=null && indents.extraCosts.size>0) {
                indents.extraCosts[0].amount?.let {
                    mBinding.extCostLay.visibility = View.VISIBLE
                    mBinding.valExtCost.text = it
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return extraAmount
    }

}