package com.pojo.droptruck.user.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pojo.droptruck.activity.indents.CreIndentRes
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.repository.CustomerApiRepository
import com.pojo.droptruck.user.model.UserCreateIndent
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserIndentsViewModel @Inject constructor(var repo: CustomerApiRepository): ViewModel() {

    var cIndent = UserCreateIndent()
    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val cIResultLiveData = MutableLiveData<Resource<CreIndentRes>>()
    
    fun callCreateIntent(cInt: UserCreateIndent) {
        Log.d("Intent:::",Gson().toJson(cInt))
        repo.createIntent(cIResultLiveData,cInt)

    }

}