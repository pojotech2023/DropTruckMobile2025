package com.pojo.droptruck.activity.createsupplier

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.GetDriverDetails
import com.pojo.droptruck.pojo.GetSupplierDetails
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateSupplierViewModel @Inject constructor(var repo: ApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Resource<UpdateResult>>()
    val supplierDtlLiveData = MutableLiveData<Resource<GetSupplierDetails>>()

    fun callCreateSupplier(
        rbIndentId: RequestBody,
        rbUserId: RequestBody,
        rbVendor: RequestBody,
        rbSupplierType: RequestBody,
        rbCompany: RequestBody,
        rbContact: RequestBody,
        rbPanNumber: RequestBody,
        rbBankName: RequestBody,
        rbIfscCode: RequestBody,
        rbAccNum: RequestBody,
        rbConfirmAccNum: RequestBody,
        rbRemarks: RequestBody,
        panCard: MultipartBody.Part?,
        businessCard: MultipartBody.Part?,
        memo: MultipartBody.Part?,
        bankDetails: MultipartBody.Part?,
        ewayBill: MultipartBody.Part?,
        tripsInvoices: MultipartBody.Part?,
        otherDocument: MultipartBody.Part?
    ) {

        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.createSupplierAPICall(resultLiveData,rbIndentId, rbUserId, rbVendor,
                    rbSupplierType, rbCompany, rbContact, rbPanNumber, rbBankName,rbIfscCode,
                    rbAccNum, rbConfirmAccNum, rbRemarks, panCard, businessCard, memo,
                    bankDetails, ewayBill, tripsInvoices, otherDocument)

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun callGetSupplierDetails(mobileNum: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.getSupplierDetailsAPI(mobileNum,supplierDtlLiveData)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}