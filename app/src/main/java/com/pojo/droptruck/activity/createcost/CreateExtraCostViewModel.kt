package com.pojo.droptruck.activity.createcost

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateExtraCostViewModel @Inject constructor(var repo: ApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()

    fun callCreateExtraCost(
        rbIndentId: RequestBody,
        rbUserId: RequestBody,
        rbAmount: RequestBody,
        rbCostType: RequestBody,
        bill_copy: MultipartBody.Part?,
        unloading_photo: MultipartBody.Part?,
        bill_copies_info: MultipartBody.Part?
    ) {

        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.createExtraCostAPICall(resultLiveData,rbIndentId, rbUserId, rbAmount,rbCostType
                    ,bill_copy, unloading_photo, bill_copies_info)

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun callCreateExtraCostWithoutImage(
        rbIndentId: RequestBody,
        rbUserId: RequestBody,
        rbAmount: RequestBody,
        rbCostType: RequestBody
    ) {

        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.createExtraCostAPICall(resultLiveData,rbIndentId, rbUserId, rbAmount,rbCostType)

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}