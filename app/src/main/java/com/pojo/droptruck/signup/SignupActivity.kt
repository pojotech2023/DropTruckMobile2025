package com.pojo.droptruck.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.pojo.droptruck.databinding.ActivitySignupBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.pojo.CustomerRegistration
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : BaseActivity() {

    lateinit var mBinding: ActivitySignupBinding
    val mViewModel: SignUpViewModel by viewModels()

    var howFind = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnLayoutSignup.setOnClickListener { finish() }

        mBinding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("Item: ", "selected item " + parent.selectedItem + " " + position)

                if(position>0) {
                    howFind = parent.selectedItem.toString()
                }else {
                    howFind = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mBinding.btnReg.setOnClickListener {

            val name = mBinding.edtName.text.toString()
            val mail = mBinding.edtMail.text.toString()
            val number = mBinding.edtNumber.text.toString()
            val companyName = mBinding.edtCompany.text.toString()

            if (name.trim().isEmpty()) {
                shortToast("Enter name")
            }else if (number.trim().isEmpty()) {
                shortToast("Enter Phone Number")
            }else if (number.length != 10) {
                shortToast("Enter Valid Phone Number")
            }/*else if (mail.trim().isEmpty()) {
                shortToast("Enter Email")
            }else if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                shortToast("Enter valid Email")
            }*/else {

                showProgressDialog()
                var register = CustomerRegistration()
                register.customerName = name
                register.contactNumber = number
                register.customerEmail = mail
                register.companyName = companyName
                register.findOurApp = howFind

                mViewModel.callRegister(register)

            }

        }

        mViewModel.resultLiveData.observe(this, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    finish()
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

        try {
            if (intent!=null) {
                val mob = intent.getStringExtra("mob")
                mBinding.edtNumber.setText(mob)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}