package com.pojo.droptruck.enquiry

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnquiryViewModel @Inject constructor(var repo: EnquiryApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Resource<Enquiry>>()
    val applyRateResLiveData = MutableLiveData<Resource<UpdateResult>>()
    val cancelIndentListLiveData = MutableLiveData<Resource<Enquiry>>()
    val followUpListLiveData = MutableLiveData<Resource<Enquiry>>()
    val confirmedListLiveData = MutableLiveData<Resource<Enquiry>>()

    val deleteLiveData = MutableLiveData<Resource<UpdateResult>>()
    val restoreLiveData = MutableLiveData<Resource<UpdateResult>>()
    val cloneIndentLiveData = MutableLiveData<Resource<UpdateResult>>()

    val TAG = "QuotedFragment"

    fun getEnquiryUnQuotedList(role:String, userId:String,currentPage:Int){

        viewModelScope.launch(Dispatchers.IO) {
            //sales/indents-list/3/28
            //val url = AppUtils.BASE_URL + "sales/" + "indents-list/"+role+"/"+userId
            val url = AppUtils.BASE_URL + "sales/" + "indents-list"
            repo.callEnquiryApi(resultLiveData,"",userId,url,role,currentPage.toString())
        }

    }

    fun getSupplierQuotedList(role: String, userId: String,currentPage:Int) {
        Log.d(TAG, "getSupplierQuotedList: "+currentPage)

        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "sales/" + "quoted/"+userId
            val url = AppUtils.BASE_URL + "sales/" + "quoted"
            repo.callSupplierQuoteList(resultLiveData,url,userId,currentPage.toString())
        }

    }

    fun callApplyRate(applyRate: ApplyRate,userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = AppUtils.BASE_URL + "rates/" + "store/"+userId
            repo.callCreateRate(applyRateResLiveData,applyRate,url)
        }
    }

    fun getCancelledList(role: String, userId: String,currentPage:Int) {//becz of pagination...
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "sales/" + "cancelled-indent-list/"+userId
            val url = AppUtils.BASE_URL + "sales/" + "cancelled-indent-list"
            repo.callCancelIndentListApi(cancelIndentListLiveData,url,userId,currentPage.toString())
        }

    }

    fun getFollowUpList(role: String, userId: String,currentPage:Int) {//becz of pagination...
        //https://droptruck.pojotech.in/api/api/sales/followup-indent-list/22
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "sales/" + "followup-indent-list/"+userId
            val url = AppUtils.BASE_URL + "sales/" + "followup-indent-list"
            repo.callFollowUpListApi(followUpListLiveData,url,userId,currentPage.toString())
        }

    }
    fun getConfirmedList(role: String, userId: String,currentPage:Int) {//becz of pagination...
        //https://droptruck.pojotech.in/api/api/sales/confirmed-indent-list/3
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "sales/" + "confirmed-indent-list/"+userId
            val url = AppUtils.BASE_URL + "sales/" + "confirmed-indent-list"
            repo.callConfirmedListApi(confirmedListLiveData,url,userId,currentPage.toString())
        }
    }

    fun deleteIndent(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = AppUtils.BASE_URL + "sales/" + id + "/delete"
            repo.callDeleteIndentApi(deleteLiveData,url)
        }
    }

    fun getCustomerList(role: String, userId: String,currentPage:Int) {
        //https://droptruck.pojotech.in/api/api/customer/unquoted-indent-list/6/34
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "customer/" + "unquoted-indent-list/"+role+"/"+userId
            val url = AppUtils.BASE_URL + "customer/" + "unquoted-indent-list"
            repo.callEnquiryApi(resultLiveData,"",userId,url,role,currentPage.toString())
        }
    }

    fun getCustomerQuotedList(role: String, userId: String,currentPage:Int) {
        //https://droptruck.pojotech.in/api/api/customer/quoted-indent-list/6/34
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "customer/" + "quoted-indent-list/"+role+"/"+userId
            val url = AppUtils.BASE_URL + "customer/" + "quoted-indent-list"
            repo.callEnquiryApi(resultLiveData,"",userId,url,role,currentPage.toString())
        }
    }

    fun callRestore(id: Int, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.callRestoreIndentAPI(restoreLiveData,id,userId)
        }
    }

    fun callCloneIndent(indentId:String){
        viewModelScope.launch(Dispatchers.IO)  {
            repo.callCloneIndentAPI(cloneIndentLiveData,indentId)

        }
    }


}