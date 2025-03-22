package com.pojo.droptruck.activity.createsupplier

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pojo.droptruck.databinding.ActivityCreateSupplierBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.Suppliers
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class CreateSupplierActivity : BaseActivity() {

    lateinit var mBinding: ActivityCreateSupplierBinding
    val mViewModel: CreateSupplierViewModel by viewModels()

    var supplierType = ""

    var proofType = ""
    var indentId: String=""
    var userId:String = ""
    var role:String = ""
    val TAG = "CreateDriverActivity"

    var pan_card: MultipartBody.Part? = null
    var business_card: MultipartBody.Part? = null
    var memo: MultipartBody.Part? = null
    var bank_details: MultipartBody.Part? = null
    var eway_bill: MultipartBody.Part? = null
    var trips_invoices: MultipartBody.Part? = null
    var other_document: MultipartBody.Part? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateSupplierBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        userId = prefs.getValueString(AppConstant.USER_ID).toString()
        role = prefs.getValueString(AppConstant.ROLE_ID).toString()

        mBinding.imgBack.setOnClickListener { finish() }

        askPermission()

        try {
            if (intent!=null) {
                indentId = intent.getStringExtra("id")!!
                Log.d(TAG, "onCreate: $indentId")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        mBinding.truckTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                supplierType = parent.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mBinding.btnSubmit.setOnClickListener {

            if (isValidation()) {
                showProgressDialog()

                val userId = prefs.getValueString(AppConstant.USER_ID)

                try{
                    val rbIndentId = RequestBody.create(MediaType.parse("text/plain"), indentId)
                    val rbUserId = RequestBody.create(MediaType.parse("text/plain"), userId.toString())
                    val rbContact = RequestBody.create(MediaType.parse("text/plain"), mBinding.etContact.text.toString())
                    val rbVendor = RequestBody.create(MediaType.parse("text/plain"), mBinding.etCustomer.text.toString())
                    val rbCompany= RequestBody.create(MediaType.parse("text/plain"), mBinding.etCompany.text.toString())
                    val rbBankName = RequestBody.create(MediaType.parse("text/plain"), mBinding.etBankName.text.toString())
                    val rbPanNumber = RequestBody.create(MediaType.parse("text/plain"), mBinding.etPan.text.toString())
                    val rbIfscCode = RequestBody.create(MediaType.parse("text/plain"), mBinding.etNum1.text.toString())
                    val rbAccNum = RequestBody.create(MediaType.parse("text/plain"), mBinding.etAcNum.text.toString())
                    val rbConfirmAccNum = RequestBody.create(MediaType.parse("text/plain"), mBinding.etConfirmAccNum.text.toString())
                    val rbRemarks = RequestBody.create(MediaType.parse("text/plain"), mBinding.etRemarks.text.toString())
                    val rbSupplierType = RequestBody.create(MediaType.parse("text/plain"), supplierType)

                    mViewModel.callCreateSupplier(rbIndentId,rbUserId,rbVendor,rbSupplierType,rbCompany
                        ,rbContact,rbPanNumber,rbBankName,rbIfscCode,rbAccNum,rbConfirmAccNum,rbRemarks
                        ,pan_card,business_card,memo,bank_details,eway_bill,trips_invoices,other_document)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        }

        mBinding.imgDoc.setOnClickListener {
            proofType = AppConstant.OTHER_DOC
            openImagePicker()
        }
        mBinding.imgPan.setOnClickListener {
            proofType = AppConstant.PAN_CARD
            openImagePicker()
        }
        mBinding.imgBill.setOnClickListener {
            proofType = AppConstant.E_BILL
            openImagePicker()
        }
        mBinding.imgInovice.setOnClickListener {
            proofType = AppConstant.TRIPS_INVOICE
            openImagePicker()
        }
        mBinding.imgBusiness.setOnClickListener {
            proofType = AppConstant.BUSINESS_CARD
            openImagePicker()
        }
        mBinding.imgOther.setOnClickListener {
            proofType = AppConstant.OTHERS
            openImagePicker()
        }
        mBinding.imgBank.setOnClickListener {
            proofType = AppConstant.BANK_DTL
            openImagePicker()
        }

        mViewModel.resultLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data!=null) {
                            shortToast(it.data.message!!)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        shortToast(it.message!!)
                    }
                }
            }
        })

        mBinding.etPan.filters = arrayOf<InputFilter>(AllCaps(),InputFilter.LengthFilter(10))
        mBinding.etNum1.filters = arrayOf<InputFilter>(AllCaps(),InputFilter.LengthFilter(11))

        mBinding.etContact.addTextChangedListener(textWatcher)

        mViewModel.supplierDtlLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            if (it.data.status == 200) {
                                setVendorDetails(it.data.data)
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

    private fun isValidation(): Boolean {
        if (mBinding.etContact.text.toString().trim().isEmpty()) {
            shortToast("Enter Contact Number")
            return false
        }else if (mBinding.etContact.text.toString().length !=10) {
            shortToast("Enter Valid Contact Number")
            return false
        }else if (mBinding.etCustomer.text.toString().trim().isEmpty()) {
            shortToast("Enter Vendor Name")
            return false
        }else if (mBinding.etCompany.text.toString().trim().isEmpty()){
            shortToast("Enter Company Name")
            return false
        }else if (mBinding.etBankName.text.toString().trim().isEmpty()){
            shortToast("Enter Bank Name")
            return false
        }else if (mBinding.etPan.text.toString().trim().isEmpty()
            || mBinding.etPan.text.toString().length!=10){
            shortToast("Enter Valid Pan Card Number")
            return false
        }else if (!AppUtils.isValidPanCardNo(mBinding.etPan.text.toString())){
            shortToast("Enter Valid Pan Card Number")
            return false
        }else if (mBinding.etNum1.text.toString().trim().isEmpty()){
            shortToast("Enter Ifsc Code")
            return false
        }else if (!AppUtils.isValidIFSCCode(mBinding.etNum1.text.toString())){
            shortToast("Enter valid Ifsc Code")
            return false
        }else if (mBinding.etAcNum.text.toString().trim().isEmpty()){
            shortToast("Enter Account Number")
            return false
        }else if (mBinding.etConfirmAccNum.text.toString().trim().isEmpty()){
            shortToast("Enter Confirm Account Number")
            return false
        }else if (!(mBinding.etAcNum.text.toString().equals(
                mBinding.etConfirmAccNum.text.toString()))){
            shortToast("Account Number and Confirm Account Number should be same")
            return false
        }
        else if (mBinding.truckTypeSpinner.selectedItemPosition==0){
            shortToast("Select Vendor Type")
            return false
        }else if (pan_card == null) {
            shortToast("Upload Pan Card Image")
            return false
        }/*else if (business_card == null) {
            shortToast("Upload Business Card Image")
            return false
        }else if (memo == null) {
            shortToast("Upload Other Image")
            return false
        }*/else if (bank_details == null) {
            shortToast("Upload Bank DetailsImage")
            return false
        }else if (eway_bill == null) {
            shortToast("Upload EWay Bill")
            return false
        }else if (trips_invoices == null) {
            shortToast("Upload Invoices")
            return false
        }/*else if (other_document == null) {
            shortToast("Upload Other Documents")
            return false
        }*/

        else {
            return true
        }

    }

    private fun askPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES
            )
            .withListener(
                object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                }
            )
            .check()
    }

    private fun openImagePicker() {

        ImagePicker.with(this)//.crop()
            .compress(512)
            .maxResultSize(1080, 1080).start()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK

            if (data!=null && data.data != null) {
                val uri: Uri = data.data!!
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

                when(proofType) {
                    AppConstant.PAN_CARD -> {
                        //mBinding.imgVehicle.setImageURI(uri)
                        mBinding.imgPan.setImageBitmap(bitmap)
                        pan_card = convertMultiPart(bitmap,"pan_card")
                    }
                    AppConstant.OTHER_DOC -> {
                        mBinding.imgDoc.setImageURI(uri)
                        other_document = convertMultiPart(bitmap,"other_document")
                    }
                    AppConstant.BANK_DTL -> {
                        mBinding.imgBank.setImageURI(uri)
                        bank_details = convertMultiPart(bitmap,"bank_details")
                    }
                    AppConstant.E_BILL -> {
                        mBinding.imgBill.setImageURI(uri)
                        eway_bill = convertMultiPart(bitmap,"eway_bill")
                    }
                    AppConstant.OTHERS -> {
                        mBinding.imgOther.setImageURI(uri)
                        memo = convertMultiPart(bitmap,"memo")
                    }
                    AppConstant.BUSINESS_CARD -> {
                        mBinding.imgBusiness.setImageURI(uri)
                        business_card = convertMultiPart(bitmap,"business_card")
                    }
                    AppConstant.TRIPS_INVOICE -> {
                        mBinding.imgInovice.setImageURI(uri)
                        trips_invoices = convertMultiPart(bitmap,"trips_invoices")
                    }
                }
            }else {
                shortToast("No data found")
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            shortToast(ImagePicker.getError(data))
        } else {
            Log.d("ImagePicker", "Task Cancelled")
        }
    }

    fun convertMultiPart(bitmap: Bitmap,name:String): MultipartBody.Part {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()

        val fileBody: RequestBody =
            RequestBody.create(MultipartBody.FORM, image)
        return MultipartBody.Part.createFormData(name, name, fileBody)

    }

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (s.toString().isNotEmpty()) {
                    if (s.toString().length ==10) {
                       showProgressDialog()
                        mViewModel.callGetSupplierDetails(s.toString())
                    }
                }
            }catch (e:Exception){
                
                e.printStackTrace()
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    private fun setVendorDetails(data: Suppliers?) {

        data?.let { vDtl ->
            try {

                vDtl.supplierName?.let { mBinding.etCustomer.setText(it) }
                vDtl.companyName?.let { mBinding.etCompany.setText(it) }
                vDtl.panCardNumber?.let { mBinding.etPan.setText(it) }
                vDtl.bankName?.let { mBinding.etBankName.setText(it) }
                vDtl.ifscCode?.let { mBinding.etNum1.setText(it) }
                vDtl.accountNumber?.let { mBinding.etAcNum.setText(it) }
                vDtl.reAccountNumber?.let { mBinding.etConfirmAccNum.setText(it) }
                vDtl.remarks?.let { mBinding.etRemarks.setText(it) }

                try{
                    mBinding.truckTypeSpinner.setSelection((mBinding.truckTypeSpinner.getAdapter() as ArrayAdapter<String>)
                        .getPosition(vDtl.supplierType.toString()))
                }catch (e:Exception){
                    e.printStackTrace()
                }

                vDtl.panCard?.let {
                    setImages(AppUtils.IMAGE_BASE_URL+it,
                        mBinding.imgPan,AppConstant.PAN_CARD)
                }
                vDtl.businessCard?.let {
                    try{
                        val jsonArray = JSONArray(it)
                        val img = jsonArray.getString(0)
                        Log.d("Image:::",img)
                        setImages(AppUtils.IMAGE_BASE_URL+img,
                            mBinding.imgBusiness,AppConstant.BUSINESS_CARD)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                vDtl.ewayBill?.let {
                    setImages(AppUtils.IMAGE_BASE_URL+it,
                        mBinding.imgBill,AppConstant.E_BILL)
                }
                vDtl.tripsInvoices?.let {
                    setImages(AppUtils.IMAGE_BASE_URL+it,
                        mBinding.imgInovice,AppConstant.TRIPS_INVOICE)
                }
                vDtl.otherDocument?.let {
                    setImages(AppUtils.IMAGE_BASE_URL+it,
                        mBinding.imgDoc,AppConstant.OTHER_DOC)
                }
                vDtl.bankDetails?.let {
                    setImages(AppUtils.IMAGE_BASE_URL+it,
                        mBinding.imgBank,AppConstant.BANK_DTL)
                }

                vDtl.memo?.let {
                    setImages(AppUtils.IMAGE_BASE_URL+it,
                        mBinding.imgOther,AppConstant.OTHERS)
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    private fun setImages(imagePath: String, imageView: ImageView, type: String) {
        try {
            Glide.with(this).asBitmap().load(imagePath).into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        try {
                            imageView.setImageBitmap(bitmap)
                            when(type) {

                                AppConstant.PAN_CARD -> {
                                    pan_card = convertMultiPart(bitmap,"pan_card")
                                }
                                AppConstant.OTHER_DOC -> {
                                    other_document = convertMultiPart(bitmap,"other_document")
                                }
                                AppConstant.BANK_DTL -> {
                                    bank_details = convertMultiPart(bitmap,"bank_details")
                                }
                                AppConstant.E_BILL -> {
                                    eway_bill = convertMultiPart(bitmap,"eway_bill")
                                }
                                AppConstant.OTHERS -> {
                                    memo = convertMultiPart(bitmap,"memo")
                                }
                                AppConstant.BUSINESS_CARD -> {
                                    business_card = convertMultiPart(bitmap,"business_card")
                                }
                                AppConstant.TRIPS_INVOICE -> {
                                    trips_invoices = convertMultiPart(bitmap,"trips_invoices")
                                }

                            }

                        }catch (e:Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }catch (e:Exception){
            e.printStackTrace()
        }

    }


}