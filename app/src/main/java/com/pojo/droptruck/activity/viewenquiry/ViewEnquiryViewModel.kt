package com.pojo.droptruck.activity.viewenquiry

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.enquiry.EnquiryApiRepository
import com.pojo.droptruck.pojo.DriverDetails
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewEnquiryViewModel @Inject constructor(var repo: EnquiryApiRepository): ViewModel() {

    var indent = Indents()
    val resultLiveData = MutableLiveData<Resource<DriverDetails>>()

    fun setIndents(data: Indents) {
        Log.d("data", data.toString())
        indent = data
    }

    fun callDriverDetailsAPI(userId: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.callDriverAPI(resultLiveData,id,userId)
        }
    }


}