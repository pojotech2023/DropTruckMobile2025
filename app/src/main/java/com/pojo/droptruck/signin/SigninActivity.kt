package com.pojo.droptruck.signin

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mukeshsolanki.OtpView
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivitySigninBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SigninActivity : BaseActivity() {

    lateinit var mBinding: ActivitySigninBinding
    val mViewModel: SignInViewModel by viewModels()

    lateinit var dialog: Dialog
    var timer: CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySigninBinding.inflate(layoutInflater)
        mBinding.login = mViewModel
        mBinding.lifecycleOwner = this
        setContentView(mBinding.root)

        mViewModel.showToast.observe(this, Observer {
            if (it!=null){
                shortToast(it)
            }
        })

        mViewModel.progressDialog.observe(this, Observer {
            if (it!=null){
                if (it){
                    showProgressDialog()
                }else{
                    dismissProgressDialog()
                }
            }
        })

        mViewModel.loginLiveData.observe(this,Observer {
            if (it!=null){
                dismissProgressDialog()
                when(it.status){
                    Status.SUCCESS -> {

                        if (it.data?.status == 200) {

                            closeDialog()

                            it.data.data?.let { data ->
                                prefs.save(AppConstant.USER_ID, data.id.toString())
                                prefs.save(AppConstant.NAME, data.name.toString())
                                prefs.save(AppConstant.EMAIL, data.email.toString())
                                prefs.save(AppConstant.ROLE_ID, data.roleId.toString())
                                prefs.save(AppConstant.CONTACT, data.contact.toString())

                                if (data.roleId.toString().equals(AppConstant.CUSTOMER)){
                                    callCustomerActivity()
                                    //callNewMainActivity()
                                }else{
                                    callNewMainActivity()
                                }

                            }

                            //callNewMainActivity()

                        }

                        shortToast(it.data?.message.toString())

                    }
                    Status.ERROR -> {
                        shortToast(it.message!!)
                    }
                }
            }
        })

        mBinding.singUpNow.setOnClickListener {

            callSignUpActivity("")

        }

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.POST_NOTIFICATIONS
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

        mViewModel.customerLoginLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                dismissProgressDialog()
                                shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    callOtpDialog(it.data.otp)
                                }else {
                                    if (!(it.data.message.toString().contains("Inactive"))){
                                        callSignupPage()
                                    }
                                }
                            }else{
                                callSignupPage()
                                //shortToast("No Response")
                            }
                        }
                        Status.ERROR -> {
                            //callSignupPage()
                            shortToast(it.message!!)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        })

        //mBinding.etUserName.addTextChangedListener(textWatcher)

    }

    private fun callSignupPage() {

        try {
            callSignUpActivity(mBinding.etUserName.text.toString().trim())
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
                    if (TextUtils.isDigitsOnly(s.toString())){
                        if (s.toString().length <=10) {
                            mBinding.pwdLay.visibility = View.GONE
                        }else {
                            mBinding.pwdLay.visibility = View.VISIBLE
                        }
                    }else {
                        mBinding.pwdLay.visibility = View.VISIBLE
                    }
                }else {
                    mBinding.pwdLay.visibility = View.VISIBLE
                }
            }catch (e:Exception){
                mBinding.pwdLay.visibility = View.VISIBLE
                e.printStackTrace()
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }


    fun callOtpDialog(otp: String?) {
        dialog = showOTPDialog(R.layout.dialo_otp)

        val btnSubmit: Button = dialog.findViewById(R.id.btn_save)
        //val etOtp: EditText = dialog.findViewById(R.id.et_otp)
        val txtTimer: TextView = dialog.findViewById(R.id.txt_time)
        val txtResend: TextView = dialog.findViewById(R.id.txt_resend)
        val otpView: OtpView = dialog.findViewById(R.id.otp_view)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

        imgClose.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        startTimer(txtTimer,txtResend)

        txtResend.setOnClickListener {
            startTimer(txtTimer,txtResend)
            mViewModel.callLogin()
        }

        btnSubmit.setOnClickListener {

            if (otpView.text.toString().trim().isEmpty()
                || otpView.text.toString().length !=6) {
                shortToast("Please enter valid Otp")
            }else {

                try {
                    if (timer!=null) {
                        timer?.cancel()
                        txtTimer.visibility = View.GONE
                        txtResend.visibility = View.VISIBLE
                    }
                }catch (e:Exception){
                    txtTimer.visibility = View.GONE
                    txtResend.visibility = View.VISIBLE
                }

                showProgressDialog()
                mViewModel.callOtp(otpView.text.toString(),mBinding.etUserName.text.toString())
            }
        }

            dialog.show()
        }

    private fun startTimer(txtTimer: TextView,txtResend: TextView) {

        txtResend.visibility = View.GONE
        txtTimer.visibility = View.VISIBLE

        timer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val sec =  millisUntilFinished / 1000
                if (sec<9){
                    txtTimer.text = "00: 0" + millisUntilFinished / 1000 + " " + "Secs"
                }else{
                    txtTimer.text = "00: " + millisUntilFinished / 1000 + " " + "Secs"
                }
                //txtTimer.setText("00: " + millisUntilFinished / 1000 + " " + "Secs")
            }
            override fun onFinish() {
                txtResend.visibility = View.VISIBLE
                txtTimer.visibility = View.GONE
            }
        }.start()

    }


    fun closeDialog() {
        try {
            if (this::dialog.isInitialized) {
                dialog.dismiss()
                dialog.cancel()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}