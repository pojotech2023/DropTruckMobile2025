package com.pojo.droptruck.activity.indents

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pojo.droptruck.pojo.GetCustomerDetails
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndentsViewModel @Inject constructor(var repo: ApiRepository): ViewModel() {

    var cIndent = CreateIndentPOJO()
    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()
    val customerDtlLiveData = MutableLiveData<Resource<GetCustomerDetails>>()
    var bodyTypesLiveData: TypesPojo? = TypesPojo()
    var materialTypesLiveData : TypesPojo? = TypesPojo()
    var truckTypesLiveData : TypesPojo? = TypesPojo()

    fun callCreateIntent(cInt: CreateIndentPOJO) {
        /*if (isValidation()){
            mLoader.value = true
        }*/

        Log.d("Intent:::",Gson().toJson(cInt))
        repo.createIntent(resultLiveData,cInt)

    }

    private fun isValidation(): Boolean {

        if (cIndent.customerName.trim().isEmpty()) {
            showToast.value = "Enter Customer Name"
            return false
        }else if (cIndent.companyName.trim().isEmpty()){
            showToast.value = "Enter Company Name"
            return false
        }else if (cIndent.numberOne.trim().isEmpty()){
            showToast.value = "Enter Number One"
            return false
        }else if (cIndent.numberTwo.trim().isEmpty()){
            showToast.value = "Enter Number Two"
            return false
        }/*else if (cIndent.sourceLead.trim().isEmpty()){
            showToast.value = "Select Source Lead"
            return false
        }else if (cIndent.pickUpLocation.trim().isEmpty()){
            showToast.value = "Select PickUp Location"
            return false
        }else if (cIndent.dropLocation.trim().isEmpty()){
            showToast.value = "Select Drop Location"
            return false
        }else if (cIndent.truckType.trim().isEmpty()){
            showToast.value = "Select Truck Type"
            return false
        }else if (cIndent.bodyType.trim().isEmpty()){
            showToast.value = "Select Body Type"
            return false
        }*/else if (cIndent.weight.trim().isEmpty()){
            showToast.value = "Enter Weight"
            return false
        }/*else if (cIndent.materialType.trim().isEmpty()){
            showToast.value = "Select Material Type"
            return false
        }else if (cIndent.podSoft.trim().isEmpty()){
            showToast.value = "Enter POD Soft"
            return false
        }else if (cIndent.remarks.trim().isEmpty()){
            showToast.value = "Enter Remarks"
            return false
        }*/ else {
            return true
        }

    }

    fun callGetCustomerDetails(mob: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.getCustomerDetailsAPI(mob,customerDtlLiveData)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun getSpinnerData(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {

                val bodyType =
                    async { repo.callSpinnerData(AppUtils.BASE_URL + AppConstant.BODY_TYPES) }
                val truckType =
                    async { repo.callSpinnerData(AppUtils.BASE_URL + AppConstant.TRUCK_TYPES) }
                val matType =
                    async { repo.callSpinnerData(AppUtils.BASE_URL + AppConstant.MATERIAL_TYPES) }
                try {
                    bodyTypesLiveData = bodyType.await()
                    Log.d("BodyType:::",Gson().toJson(bodyTypesLiveData))
                    truckTypesLiveData = truckType.await()
                    materialTypesLiveData = matType.await()

                    val context = activity as IndentActivity
                    context.processData(bodyTypesLiveData, truckTypesLiveData, materialTypesLiveData)

                } catch (ex: Exception) {
                    val context = activity as IndentActivity
                    context.processData(bodyTypesLiveData, truckTypesLiveData, materialTypesLiveData)
                    ex.printStackTrace()
                }
            }
        }

    }

}