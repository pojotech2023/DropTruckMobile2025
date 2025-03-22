package com.pojo.droptruck.fragment.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.repository.CustomerApiRepository
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(var repo: CustomerApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()
    val resultLiveData =  MutableLiveData<Resource<Enquiry>>()
    
    fun getHistoryList(role: String?, userId: String, currentPage: Int) {
        //https://droptruck.pojotech.in/api/api/customer/history/34
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL+"customer/"+"history/"+userId
            val url = AppUtils.BASE_URL+"customer/"+"history"
            repo.callHistoryAPI(resultLiveData,url,userId,currentPage.toString())
        }
    }

}