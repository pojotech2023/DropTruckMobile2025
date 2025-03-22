package com.pojo.droptruck.activity.confirmindent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.ConfirmIndent
import com.pojo.droptruck.pojo.IndentDetails
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.user.model.CustIndentDetails
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConIndentViewModel @Inject constructor(var repo: ApiRepository): ViewModel() {

    val resultLiveData = MutableLiveData<Resource<ConfirmIndent>>()
    val confirmDriverLiveData = MutableLiveData<Resource<UpdateResult>>()
    val rateUpdateLiveData = MutableLiveData<Resource<UpdateResult>>()
    val deleteRateLiveData = MutableLiveData<Resource<UpdateResult>>()
    val confirmTripLiveData = MutableLiveData<Resource<UpdateResult>>()
    val cancelTripLiveData = MutableLiveData<Resource<UpdateResult>>()
    val customerIndentLiveData = MutableLiveData<Resource<CustIndentDetails>>()

    //var indent = IndentDetails()

    fun callConfirmIndent(userId: String?, indentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // sales/confirm-indent-details/28/1080
            val url = AppUtils.BASE_URL+"sales/"+"confirm-indent-details/"+userId+"/"+indentId
            repo.confirmIndent(resultLiveData,url)
        }
    }

    /*fun setIndentDetails(indentDetails: IndentDetails) {
        Log.d("data", indentDetails.toString())
        indent.pickupLocation = indentDetails.pickupLocation
        indent.dropLocation = indentDetails.dropLocation
        indent.truckType = indentDetails.truckType
        indent.bodyType = indentDetails.bodyType
        indent.weight = indentDetails.weight
        indent.weightUnit = indentDetails.weightUnit
        indent.materialType = indentDetails.materialType
        indent.salesPerson = indentDetails.salesPerson
    }*/

    fun callSelectDriverRate(id: String, rateId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.confirmDriverAmount(confirmDriverLiveData,id,rateId,userId)
        }
    }

    fun callSubmitRate(id: String, amount: String, userId: String,roleId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.customerRateUpdate(rateUpdateLiveData, roleId,id,amount,userId)
        }
    }

    fun deleteSelectedAmt(indentId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteRateAPI(deleteRateLiveData, indentId,userId)
        }
    }

    fun callConfirmTrip(indentId: String, userId: String, role: String) {
        viewModelScope.launch(Dispatchers.IO) {

            if (role.equals(AppConstant.SALES)) {
                repo.confirmTripAPI(confirmTripLiveData, indentId,userId)
            }else {
                repo.customerConfirmTripAPI(confirmTripLiveData, indentId,userId)
            }
        }
    }

    fun callCancelTrip(
        indentId: String, userId: String,
        cancelReason: String, reason: String, date: String, role: String,fReason:String
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            if (role.equals(AppConstant.SALES)) {
                repo.cancelTripAPI(cancelTripLiveData, indentId,userId,cancelReason,
                    reason,date,fReason)
            }else {
                repo.customerCancelTripAPI(cancelTripLiveData, indentId,userId,cancelReason,
                    reason,date,fReason)
            }

        }
    }

    fun callCustomerIndentDetails(indentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = AppUtils.BASE_URL+"customer/"+"get-indent-details/"+indentId
            repo.customerIndentDetails(customerIndentLiveData,url)
        }
    }

}