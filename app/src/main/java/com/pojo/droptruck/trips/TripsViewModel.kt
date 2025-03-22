package com.pojo.droptruck.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(var repo: TripsApiRepository): ViewModel() {

    val showToast = MutableLiveData<String>()
    val mLoader = MutableLiveData<Boolean>()

    val resultLiveData = MutableLiveData<Resource<Enquiry>>()
    val applyRateResLiveData = MutableLiveData<Resource<UpdateResult>>()
    val cancelIndentListLiveData = MutableLiveData<Resource<Enquiry>>()
    val loadingListLiveData = MutableLiveData<Resource<Enquiry>>()
    val unLoadingListLiveData = MutableLiveData<Resource<Enquiry>>()
    val wfdListLiveData = MutableLiveData<Resource<Enquiry>>()
    val onRoadTripLiveData = MutableLiveData<Resource<Enquiry>>()

    val podListLiveData = MutableLiveData<Resource<Enquiry>>()
    val completedListLiveData = MutableLiveData<Resource<Enquiry>>()


    fun callConfirmedTrips(userId: String?, role: String?,currentPage:Int) {
        //https://droptruck.pojotech.in/api/api/trips/confirmed-trips
        viewModelScope.launch(Dispatchers.IO) {

            var url = ""

            if (role!!.equals(AppConstant.CUSTOMER)) {
               //url = AppUtils.BASE_URL + "customer/" + "confirmed-trips/"+userId
               url = AppUtils.BASE_URL + "customer/" + "confirmed-trips"
            }else {
                //url = AppUtils.BASE_URL + "trips/" + "confirmed-trips/"+userId
                url = AppUtils.BASE_URL + "trips/" + "confirmed-trips"
            }

            repo.callTripsListApi(wfdListLiveData,url,userId.toString(),currentPage.toString())
        }
    }

    fun callLoadingTrips(userId: String?, role: String?,currentPage:Int) {
        //trips/loading-trips/2
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "trips/" + "loading-trips/"+userId
            val url = AppUtils.BASE_URL + "trips/" + "loading-trips"
            repo.callTripsListApi(loadingListLiveData,url,userId.toString(),currentPage.toString())
        }
    }

    fun callOnRoadTrips(userId: String?, role: String?,currentPage:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "trips/" + "ontheroad-loading/"+userId
            val url = AppUtils.BASE_URL + "trips/" + "ontheroad-loading"
            repo.callTripsListApi(onRoadTripLiveData,url,userId.toString(),currentPage.toString())
        }
    }

    fun callUnLoadingTrips(userId: String?, role: String?,currentPage:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "trips/" + "unloading-trips/"+userId
            val url = AppUtils.BASE_URL + "trips/" + "unloading-trips"
            repo.callTripsListApi(unLoadingListLiveData,url,userId.toString(),currentPage.toString())
        }
    }

    fun callPODTripsList(userId: String?, role: String?,currentPage:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //val url = AppUtils.BASE_URL + "trips/" + "pod-list/"+userId
            val url = AppUtils.BASE_URL + "trips/" + "pod-list"
            repo.callTripsListApi(podListLiveData,url,userId.toString(),currentPage.toString())
        }
    }

    fun callCompleteTripsList(userId: String?, role: String?,currentPage:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //https://droptruck.pojotech.in/api/api/trips/completed-trips-list/3
            //val url = AppUtils.BASE_URL + "trips/" + "completed-trips-list/"+userId
            val url = AppUtils.BASE_URL + "trips/" + "completed-trips-list"
            repo.callTripsListApi(completedListLiveData,url,userId.toString(),currentPage.toString())
        }
    }


}