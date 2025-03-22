package com.pojo.droptruck.activity.pod

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
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pojo.droptruck.R
import com.pojo.droptruck.activity.createcost.CreateExtraCostViewModel
import com.pojo.droptruck.databinding.ActivityCreateExtraCostBinding
import com.pojo.droptruck.databinding.ActivityCreatePodactivityBinding
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
class CreatePODActivity : BaseActivity() {

    lateinit var mBinding: ActivityCreatePodactivityBinding
    val mViewModel: CreatePODViewModel by viewModels()

    var proofType = ""
    var indentId: String=""
    var userId:String = ""
    var role:String = ""

    var pod_courier: MultipartBody.Part? = null
    var pod_soft_copy: MultipartBody.Part? = null

    val TAG = "CreatePODActivity"
    var pod = ""
    var pod_receipt: MultipartBody.Part? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreatePodactivityBinding.inflate(layoutInflater)
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

        mBinding.imgPodSoftCopy.setOnClickListener {
            proofType = AppConstant.POD_SOFT_COPY
            openImagePicker()
        }
        mBinding.imgPodCourier.setOnClickListener {
            proofType = AppConstant.POD_COURIER
            openImagePicker()
        }

        mBinding.imgPodReceipt.setOnClickListener {
            proofType = AppConstant.POD_RECEIPT
            openImagePicker()
        }

        mBinding.btnSubmit.setOnClickListener {

            if (pod.isNotEmpty()) {

                val rbIndentId = RequestBody.create(MediaType.parse("text/plain"), indentId)
                val rbUserId = RequestBody.create(MediaType.parse("text/plain"), userId)
                val withData = RequestBody.create(MediaType.parse("text/plain"), pod)

                when(pod) {
                    "1" -> {

                        val cNum = RequestBody.create(MediaType.parse("text/plain"),"")

                        if (pod_soft_copy == null) {
                            shortToast("Upload POD Soft Copy Proof")
                        }else {
                            pod_courier = null
                            pod_receipt = null
                            mBinding.imgPodCourier.setImageResource(R.drawable.identity_placeholder)
                            mBinding.imgPodReceipt.setImageResource(R.drawable.identity_placeholder)
                            showProgressDialog()
                            mViewModel.podWithData(rbIndentId,rbUserId,withData,
                                cNum,pod_soft_copy,pod_courier,pod_receipt)
                        }

                    }
                    "2" -> {
                        if (mBinding.etConfirmAccNum.text.toString().trim().isEmpty()) {
                            shortToast("Enter Courier Number")
                        }else if (pod_courier == null) {
                            shortToast("Upload POD Courier Proof")
                        }else if (pod_receipt == null) {
                            shortToast("Upload POD Receipt")
                        }else {
                            pod_soft_copy = null
                            mBinding.imgPodSoftCopy.setImageResource(R.drawable.identity_placeholder)
                            showProgressDialog()
                            val courierNumber = RequestBody.create(MediaType.parse("text/plain"),
                                mBinding.etConfirmAccNum.text.toString())

                            mViewModel.podWithData(rbIndentId,rbUserId,withData,
                                courierNumber,pod_soft_copy,pod_courier,pod_receipt)
                        }
                    }
                    "3" -> {
                        showProgressDialog()
                        mViewModel.podWithOutData(indentId,userId,"3")
                    }
                }

            }else {
                shortToast("Select POD Type")
            }

        }

        mBinding.btnSubmitWithoutData.setOnClickListener {

            mViewModel.podWithOutData(indentId,userId,"0")

        }

        mBinding.podSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)
                if (position>0) {
                    pod = position.toString()
                    when(position) {
                        1 -> {
                            mBinding.podSoft.visibility = View.VISIBLE
                            mBinding.tilReceipt.visibility = View.GONE
                            mBinding.podCourier.visibility = View.GONE
                            mBinding.podReceipt.visibility = View.GONE
                        }
                        2 -> {
                            mBinding.tilReceipt.visibility = View.VISIBLE
                            mBinding.podCourier.visibility = View.VISIBLE
                            mBinding.podSoft.visibility = View.GONE
                            mBinding.podReceipt.visibility = View.VISIBLE
                        }
                        3 -> {
                            mBinding.tilReceipt.visibility = View.GONE
                            mBinding.podCourier.visibility = View.GONE
                            mBinding.podSoft.visibility = View.GONE
                            mBinding.podReceipt.visibility = View.GONE
                        }
                    }

                    //parent.selectedItem.toString()
                }else {
                    pod = ""
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
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
                    AppConstant.POD_SOFT_COPY -> {
                        Glide.with(this).load(uri).into(mBinding.imgPodSoftCopy)
                        //mBinding.imgPodSoftCopy.setImageBitmap(bitmap)
                        pod_soft_copy = convertMultiPart(bitmap,"pod_soft_copy")
                    }
                    AppConstant.POD_COURIER -> {
                        mBinding.imgPodCourier.setImageURI(uri)
                        pod_courier = convertMultiPart(bitmap,"pod_courier")
                    }
                    AppConstant.POD_RECEIPT -> {
                        mBinding.imgPodReceipt.setImageURI(uri)
                        pod_receipt = convertMultiPart(bitmap,"pod_courier_photo")
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