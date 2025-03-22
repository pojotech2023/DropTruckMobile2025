package com.pojo.droptruck.activity.driver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.GetDriverDetails
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateDriverViewModel @Inject constructor(var repo: ApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()


    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()
    val driverDtlLiveData = MutableLiveData<Resource<GetDriverDetails>>()

    fun callCreateDriver(createDriver: CreateDriver) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createDriverAPICall(resultLiveData,createDriver)
        }
    }

    fun callCreateDriver(
        rbIndentId: RequestBody, rbUserId: RequestBody, rbDriverName: RequestBody,
        rbDriverNumber: RequestBody, rbVehicleNumber: RequestBody, rbBaseLocation: RequestBody,
        rbTruckType: RequestBody, rbVehicleType: RequestBody, vehiclePhoto: MultipartBody.Part?,
        rcbPhoto: MultipartBody.Part?, dlPhoto: MultipartBody.Part?, insPhoto: MultipartBody.Part?)
    {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.createDriverAPICall(resultLiveData,rbIndentId, rbUserId, rbDriverName,
                    rbDriverNumber, rbVehicleNumber, rbBaseLocation, rbTruckType, rbVehicleType,
                    vehiclePhoto, rcbPhoto, dlPhoto, insPhoto)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun callGetDriverDetails(vehicleNum:String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.getDriverDetailsAPI(vehicleNum,driverDtlLiveData)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}