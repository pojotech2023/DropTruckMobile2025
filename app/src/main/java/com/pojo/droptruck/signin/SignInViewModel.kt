package com.pojo.droptruck.signin

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.datastore.DataStorage
import com.pojo.droptruck.pojo.CustomerLogin
import com.pojo.droptruck.pojo.Login
import com.pojo.droptruck.pojo.LoginResults
import com.pojo.droptruck.pojo.VersionResults
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel  @Inject constructor(val repo: ApiRepository, var dataStore: DataStorage) :ViewModel() {

    val user = Login()
    val showToast = MutableLiveData<String>()
    val progressDialog = MutableLiveData<Boolean>()
    val loginLiveData = MutableLiveData<Resource<LoginResults>>()
    val customerLoginLiveData = MutableLiveData<Resource<CustomerLogin>>()
    val versionLiveData = MutableLiveData<Resource<VersionResults>>()
    val checkStatusLiveData = MutableLiveData<Resource<LoginResults>>()

    fun callLogin(){

        if (user.email.isNotEmpty() && TextUtils.isDigitsOnly(user.email)
            && user.email.length==10) {
            progressDialog.value = true
            repo.customerLogin(user.email,customerLoginLiveData)
        }else {
            showToast.value = "Enter Valid Phone Number"
        }
        /*else {
            if (isValidation()) {
                Log.d("Login",user.email + " " + user.password)
                progressDialog.value = true
                repo.callLogin(user,loginLiveData)
            }
        }*/

    }

    private fun isValidation(): Boolean {
        if (user.email.trim().isEmpty()) {
            showToast.value = "Enter Email Or Phone number"
            return false
        }else if (user.password.trim().isEmpty()) {
            showToast.value = "Enter Password"
            return false
        }else {
            return true
        }
    }

    fun callOtp(otp: String, mobile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.otpVerification(otp,loginLiveData,mobile)
        }
    }

    fun callVersionCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.callVersionCheck(versionLiveData)
        }
    }

    fun checkUserStatus(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = AppUtils.BASE_URL+"check-customer-status/"+userId
            repo.callUserStatusCheck(checkStatusLiveData,url)
        }
    }

    fun updateFCMToken(userId: String,role:String,fcm:String) {
        Log.d("FCM Result","Api call")
        viewModelScope.launch(Dispatchers.IO) {
            val url = AppUtils.BASE_URL+"get-device-token"
            repo.updateFCMToken(url,userId,role,fcm)
        }
    }

}