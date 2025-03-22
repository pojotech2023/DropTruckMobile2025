package com.pojo.droptruck.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.CustomerRegistration
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val repo: ApiRepository):ViewModel() {

    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()

    fun callRegister(register: CustomerRegistration) {

        viewModelScope.launch(Dispatchers.IO) {
            repo.callCustomerRegistration(register,resultLiveData)
        }

    }


}