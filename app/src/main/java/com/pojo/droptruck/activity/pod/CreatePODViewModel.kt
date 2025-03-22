package com.pojo.droptruck.activity.pod

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreatePODViewModel @Inject constructor(var repo: ApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()

    fun podWithOutData(indentId: String, userId: String, s: String) {

        viewModelScope.launch {
            repo.podWithOutDataAPI(indentId, userId, s,resultLiveData)
        }

    }

    fun podWithData(
        indentId: RequestBody,
        userId: RequestBody,
        s: RequestBody,
        courierNumber: RequestBody,
        podSoftCopy: MultipartBody.Part?,
        podCourier: MultipartBody.Part?,
        pod_courier_photo: MultipartBody.Part?
    ) {

        viewModelScope.launch {
            repo.podWithDataAPI(indentId, userId, s,courierNumber,
                    podSoftCopy,podCourier,resultLiveData,pod_courier_photo)
        }
    }

}