package com.pojo.droptruck.activity.createcost

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pojo.droptruck.databinding.ActivityCreateExtraCostBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class CreateExtraCostActivity : BaseActivity() {

    lateinit var mBinding: ActivityCreateExtraCostBinding
    val mViewModel: CreateExtraCostViewModel by viewModels()

    var proofType = ""
    var costType = ""
    var indentId: String=""
    var userId:String = ""
    var role:String = ""

    var bill_copies_info: MultipartBody.Part? = null
    var unloading_photo: MultipartBody.Part? = null
    var bill_copy: MultipartBody.Part? = null


    val TAG = "CreateExtraCostActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateExtraCostBinding.inflate(layoutInflater)
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
                costType = parent.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mBinding.btnSubmit.setOnClickListener {

            /*if (mBinding.truckTypeSpinner.selectedItemPosition==0){
                shortToast("Select Extra Cost Type")
            }else */if (mBinding.etConfirmAccNum.text.toString().trim().isEmpty()){
                shortToast("Enter Amount")
            }else {

                try{
                    val rbIndentId = RequestBody.create(MediaType.parse("text/plain"), indentId)
                    val rbUserId = RequestBody.create(MediaType.parse("text/plain"), userId)
                    val rbAmount = RequestBody.create(MediaType.parse("text/plain"), mBinding.etConfirmAccNum.text.toString())
                    val rbCostType = RequestBody.create(MediaType.parse("text/plain"), costType)

                    if (costType.equals("None",true) ||
                        mBinding.truckTypeSpinner.selectedItemPosition == 0) {

                        if (bill_copy==null) {
                            shortToast("Select Bill Copy Image")
                        }else if (bill_copies_info==null) {
                            shortToast("Select Bill Copy Information Image")
                        }else if (unloading_photo==null) {
                            shortToast("Select UnLoading Image")
                        }else{
                            showProgressDialog()
                            mViewModel.callCreateExtraCostWithoutImage(rbIndentId,rbUserId,
                                rbAmount,rbCostType)
                        }
                    }else {
                        showProgressDialog()
                        mViewModel.callCreateExtraCost(rbIndentId,rbUserId,rbAmount,rbCostType
                            ,bill_copy,unloading_photo,bill_copies_info)
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

                // mViewModel.callCreateDriver(createDriver)

            }

        }

        mBinding.imgBill.setOnClickListener {
            proofType = AppConstant.BILL
            openImagePicker()
        }
        mBinding.imgBillInfo.setOnClickListener {
            proofType = AppConstant.BILL_INFO
            openImagePicker()
        }
        mBinding.imgUnloading.setOnClickListener {
            proofType = AppConstant.UNLOAD_PHOTO
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
                    AppConstant.BILL -> {
                        //mBinding.imgVehicle.setImageURI(uri)
                        mBinding.imgBill.setImageBitmap(bitmap)
                        bill_copy = convertMultiPart(bitmap,"bill_copy")
                    }
                    AppConstant.BILL_INFO -> {
                        mBinding.imgBillInfo.setImageURI(uri)
                        unloading_photo = convertMultiPart(bitmap,"unloading_photo")
                    }
                    AppConstant.UNLOAD_PHOTO -> {
                        mBinding.imgUnloading.setImageURI(uri)
                        bill_copies_info = convertMultiPart(bitmap,"bill_copies_info")
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

    fun convertMultiPart(bitmap: Bitmap, name:String): MultipartBody.Part {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()

        val fileBody: RequestBody =
            RequestBody.create(MultipartBody.FORM, image)
        return MultipartBody.Part.createFormData(name, name, fileBody)

    }

}