package com.pojo.droptruck.fragment.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.repository.CustomerApiRepository
import com.pojo.droptruck.user.model.ProfileUpdate
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(var repo:CustomerApiRepository) : ViewModel() {

    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()
    val delAccResultLiveData = MutableLiveData<Resource<UpdateResult>>()

    fun profileUpdate(profileUpdated: ProfileUpdate) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.callProfileAPI(resultLiveData,profileUpdated)
        }

    }

    fun delAccount(value:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.callDelAccAPI(delAccResultLiveData,value)
        }

    }


}