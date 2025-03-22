package com.pojo.droptruck.activity.driver

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivityCreateDriverBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.Driver
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class CreateDriverActivity : BaseActivity() {

    lateinit var mBinding: ActivityCreateDriverBinding
    val mViewModel: CreateDriverViewModel by viewModels()

    var truckType = ""
    var bodyType = ""

    var proofType = ""
    var indentId: String=""
    var userId:String = ""
    var role:String = ""
    val TAG = "CreateDriverActivity"

    /*var vehiclePhoto = ""
    var rcbPhoto = ""
    var dlPhoto = ""
    var insPhoto = ""*/

    /*var vehiclePhoto: RequestBody? = null
    var rcbPhoto: RequestBody? = null
    var dlPhoto : RequestBody? = null
    var insPhoto: RequestBody? = null*/

    var vehiclePhoto: MultipartBody.Part? = null
    var rcbPhoto: MultipartBody.Part? = null
    var dlPhoto : MultipartBody.Part? = null
    var insPhoto: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateDriverBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        userId = prefs.getValueString(AppConstant.USER_ID).toString()
        role = prefs.getValueString(AppConstant.ROLE_ID).toString()

        mBinding.imgBack.setOnClickListener { finish() }

        try {
            if (intent!=null) {
                indentId = intent.getStringExtra("id")!!
                Log.d(TAG, "onCreate: $indentId")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

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


        mBinding.btnSubmit.setOnClickListener {

            if (mBinding.etCustomer.text.toString().trim().isEmpty()) {
                shortToast("Enter Driver Name")
            }else if (mBinding.etCompany.text.toString().trim().isEmpty()){
                shortToast("Enter Driver Number")
            }else if (mBinding.etCompany.text.toString().length !=10){
                shortToast("Enter Valid Driver Number")
            }else if (mBinding.etNum1.text.toString().trim().isEmpty()){
                shortToast("Enter Vehicle Number")
            }else if (mBinding.baseLoc.text.toString().trim().isEmpty()){
                shortToast("Enter Location")
            }else if (mBinding.truckTypeSpinner.selectedItemPosition == 0){
                shortToast("Select Truck Type")
            }else if (mBinding.bodyTypeSpinner.selectedItemPosition == 0){
                shortToast("Enter Body Type")
            }else if (vehiclePhoto == null) {
                shortToast("Upload Vehicle Photo")
            }else if (rcbPhoto == null) {
                shortToast("Upload RcBook Photo")
            }else if (dlPhoto == null) {
                shortToast("Upload Driving License Photo")
            }else if (insPhoto == null) {
                shortToast("Upload Insurance Photo")
            }
            else {

                showProgressDialog()

                val userId = prefs.getValueString(AppConstant.USER_ID)

                try{
                    val rbIndentId = RequestBody.create(MediaType.parse("text/plain"), indentId)
                    val rbUserId = RequestBody.create(MediaType.parse("text/plain"), userId.toString())
                    val rbDriverName = RequestBody.create(MediaType.parse("text/plain"), mBinding.etCustomer.text.toString())
                    val rbDriverNumber = RequestBody.create(MediaType.parse("text/plain"), mBinding.etCompany.text.toString())
                    val rbVehicleNumber = RequestBody.create(MediaType.parse("text/plain"), mBinding.etNum1.text.toString())
                    val rbDriverBaseLocation = RequestBody.create(MediaType.parse("text/plain"), mBinding.baseLoc.text.toString())
                    val rbTruckType = RequestBody.create(MediaType.parse("text/plain"), truckType)
                    val rbVehicleType = RequestBody.create(MediaType.parse("text/plain"), bodyType)

                    mViewModel.callCreateDriver(rbIndentId,rbUserId,rbDriverName,rbDriverNumber,rbVehicleNumber
                        ,rbDriverBaseLocation,rbTruckType,rbVehicleType,vehiclePhoto,rcbPhoto,dlPhoto,insPhoto)
                }catch (e:Exception){
                    e.printStackTrace()
                }

               // mViewModel.callCreateDriver(createDriver)

            }

        }

        mBinding.vpLayout.setOnClickListener {
            proofType = AppConstant.VEHICLE_PHOTO
            openImagePicker()
        }
        mBinding.rcbLayout.setOnClickListener {
            proofType = AppConstant.RC_BOOK_PHOTO
            openImagePicker()
        }
        mBinding.dlLayout.setOnClickListener {
            proofType = AppConstant.DRIVER_LIS_PHOTO
            openImagePicker()
        }
        mBinding.insLayout.setOnClickListener {
            proofType = AppConstant.INSURENCE_PHOTO
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

        mViewModel.driverDtlLiveData.observe(this, Observer {
            dismissProgressDialog()
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            if (it.data.status == 200) {
                                setDriverDetails(it.data.data)
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

        mBinding.btnGetDetails.setOnClickListener {
            if (mBinding.etNum1.text.toString().trim().isNotEmpty()) {
                showProgressDialog()
                mViewModel.callGetDriverDetails(mBinding.etNum1.text.toString())
            }else {
                shortToast("Enter Vehicle Number")
            }

        }

    }

    private fun setDriverDetails(driver: Driver?) {

        driver?.let {

            try {
                mBinding.etCustomer.setText(it.driverName)
                mBinding.etCompany.setText(it.driverNumber)
                mBinding.baseLoc.setText(it.driverBaseLocation)

                setImages(AppUtils.IMAGE_BASE_URL+it.vehiclePhoto,
                    mBinding.imgVehicle,AppConstant.VEHICLE_PHOTO)

                setImages(AppUtils.IMAGE_BASE_URL+it.driverLicense,
                    mBinding.imgDl,AppConstant.DRIVER_LIS_PHOTO)

                setImages(AppUtils.IMAGE_BASE_URL+it.rcBook,
                    mBinding.imgRcb,AppConstant.RC_BOOK_PHOTO)

                setImages(AppUtils.IMAGE_BASE_URL+it.insurance,
                    mBinding.imgIns,AppConstant.INSURENCE_PHOTO)

                try{
                    /*mBinding.truckTypeSpinner.setSelection((mBinding.truckTypeSpinner.getAdapter() as ArrayAdapter<String>)
                        .getPosition(vDtl.supplierType.toString()))*/

                    it.truckType?.let { pos ->
                        mBinding.truckTypeSpinner.setSelection(pos.toInt())
                    }
                    mBinding.bodyTypeSpinner.setSelection((mBinding.bodyTypeSpinner.getAdapter() as ArrayAdapter<String>)
                        .getPosition(it.vehicleType.toString()))
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

        }

    }

    private fun setImages(imagePath: String,imageView: ImageView,type: String) {

        try {

            Glide.with(this)
                .asBitmap()
                .load(imagePath)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {

                        try {

                            imageView.setImageBitmap(bitmap)
                            when(type) {

                                AppConstant.VEHICLE_PHOTO -> {
                                    vehiclePhoto = convertMultiPart(bitmap,"vehicle_photo")
                                }
                                AppConstant.RC_BOOK_PHOTO -> {
                                    rcbPhoto = convertMultiPart(bitmap,"rc_book")
                                }
                                AppConstant.DRIVER_LIS_PHOTO -> {
                                    dlPhoto = convertMultiPart(bitmap,"driver_license")
                                }
                                AppConstant.INSURENCE_PHOTO -> {
                                    insPhoto = convertMultiPart(bitmap,"insurance")
                                }

                            }

                        }catch (e:Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun openImagePicker() {

        ImagePicker.with(this)//.crop()
            .compress(1024)
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
                    AppConstant.VEHICLE_PHOTO -> {
                        //{"status":422,"error":{"vehicle_photo":["The vehicle photo field is required."],"rc_book":["The rc book field is required."],"insurance":["The insurance field is required."],"driver_license":["The driver license field is required."]}}
                        //mBinding.imgVehicle.setImageURI(uri)
                        mBinding.imgVehicle.setImageBitmap(bitmap)
                        vehiclePhoto = convertMultiPart(bitmap,"vehicle_photo")
                    }
                    AppConstant.RC_BOOK_PHOTO -> {
                        mBinding.imgRcb.setImageURI(uri)
                        rcbPhoto = convertMultiPart(bitmap,"rc_book")
                    }
                    AppConstant.DRIVER_LIS_PHOTO -> {
                        mBinding.imgDl.setImageURI(uri)
                        dlPhoto = convertMultiPart(bitmap,"driver_license")
                    }
                    AppConstant.INSURENCE_PHOTO -> {
                        mBinding.imgIns.setImageURI(uri)
                        insPhoto = convertMultiPart(bitmap,"insurance")
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


    fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    fun convertMultiPart(bitmap: Bitmap,name:String): MultipartBody.Part {

        //val fileBody = RequestBody.create(MediaType.parse("image/*"),
        /*val fileBody = RequestBody.create(MediaType.parse("text/plain"), "data:image/png;base64,"+
            getStringImage(bitmap))*/

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()

        val fileBody: RequestBody =
            RequestBody.create(MultipartBody.FORM, image)
        return MultipartBody.Part.createFormData(name, name, fileBody)

    }

}