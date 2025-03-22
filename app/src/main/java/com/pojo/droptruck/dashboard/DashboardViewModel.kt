package com.pojo.droptruck.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.pojo.IndentsCounts
import com.pojo.droptruck.repository.ApiRepository
import com.pojo.droptruck.repository.CustomerApiRepository
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(var repo:CustomerApiRepository,val apiRepo: ApiRepository) : ViewModel() {

    val resultLiveData = MutableLiveData<Resource<EnqCount>>()
    val countLiveData = MutableLiveData<Resource<IndentsCounts>>()

    fun getEnqCount(userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = AppUtils.BASE_URL + "get-indents-count/" +userId
            repo.callGetCountAPI(resultLiveData,url)
        }

    }

    fun getIndentsCount(userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            //https://droptruck.pojotech.in/api/api/indents-count/3
            val url = AppUtils.BASE_URL + "indents-count/" +userId
            apiRepo.getIndentCounts(countLiveData,url)
        }

    }

}