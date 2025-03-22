package com.pojo.droptruck.fragment.profile

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.FragmentProfileBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.enquiry.EnquiryViewModel
import com.pojo.droptruck.prefs
import com.pojo.droptruck.user.model.ProfileUpdate
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.LoginActivity
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showCurrencyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    val mViewModel: ProfileViewModel by viewModels()
    val TAG = "ProfileFragment"

    lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        try {

            binding.etName.setText(prefs.getValueString(AppConstant.NAME))
            binding.etEmail.setText(prefs.getValueString(AppConstant.EMAIL))
            binding.etPhone.setText(prefs.getValueString(AppConstant.CONTACT))
            binding.etCompany.setText(prefs.getValueString(AppConstant.COMPANY_NAME))

        }catch (e:Exception ){
            e.printStackTrace()
        }

        binding.btnLogout.setOnClickListener {
            callLogout()
        }

        binding.btnSave.setOnClickListener {

            val name = binding.etName.text.toString()
            val companyName = binding.etCompany.text.toString()
            val mail = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()

            var profileUpdated = ProfileUpdate()
            profileUpdated.customerName = name
            profileUpdated.companyName = companyName
            profileUpdated.customerEmail = mail
            profileUpdated.contactNumber = phone
            profileUpdated.customerId = prefs.getValueString(AppConstant.USER_ID)!!

            showProgressDialog()
            mViewModel.profileUpdate(profileUpdated)

        }

        mViewModel.resultLiveData.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {
                                prefs.save(AppConstant.COMPANY_NAME,binding.etCompany.text.toString())
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
            }catch (e:Exception){
                e.printStackTrace()
            }
        })

        mViewModel.delAccResultLiveData.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
            try {
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            if (it.data!=null) {

                                closeDialog()
                                shortToast(it.data.message!!)
                                if (it.data.response!=null &&
                                    it.data.response == 200) {
                                    callLogout()
                                }else{
                                    shortToast(it.data.message!!)
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

        binding.btnDelAcc.setOnClickListener {
            callDelAccDialog()
        }

        return binding.root
    }

    private fun callLogout() {
        prefs.clearAll()
        requireActivity().finish()
        LoginActivity()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.resultLiveData.removeObservers(this)
        Log.d(TAG, "onDestroy: ")
    }

    fun callDelAccDialog() {
        dialog = showCurrencyDialog(R.layout.dialog_delete_account)

        val btnSubmit: Button = dialog.findViewById(R.id.btn_save)
        val etOtp: EditText = dialog.findViewById(R.id.et_otp)

        btnSubmit.setOnClickListener {
            if (etOtp.text.toString().trim().isEmpty()){
                shortToast("Enter valid phone or mail")
            }else {
                showProgressDialog()
                mViewModel.delAccount(etOtp.text.toString())
            }
        }

        dialog.show()
    }

    private fun closeDialog() {
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