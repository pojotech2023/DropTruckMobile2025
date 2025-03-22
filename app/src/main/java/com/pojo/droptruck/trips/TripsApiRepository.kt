package com.pojo.droptruck.trips

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pojo.droptruck.api.APIInterface
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.BaseResponse
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TripsApiRepository @Inject constructor(val api: APIInterface) {

    /*fun callEnquiryApi(
        docListLiveData: MutableLiveData<Resource<Enquiry>>,
        loginType: String, userId: String,url:String) {

        api.getUnQuotedList(url).enqueue(object : Callback<Enquiry> {
            override fun onResponse(call: Call<Enquiry>,
                                    response: Response<Enquiry>) {
                if (response.isSuccessful){
                    docListLiveData.postValue(Resource.success(response.body()))
                }else{
                    docListLiveData.postValue(Resource.error("Response Error",null))
                }
            }

            override fun onFailure(call: Call<Enquiry>, t: Throwable) {
                docListLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }*/


    fun callTripsListApi(
        confirmedListLiveData: MutableLiveData<Resource<Enquiry>>, url: String,
        userId: String, page: String) {

        api.getTripsList(url,userId,page).enqueue(object : Callback<Enquiry> {
            override fun onResponse(call: Call<Enquiry>,
                                    response: Response<Enquiry>) {
                if (response.isSuccessful){
                    confirmedListLiveData.postValue(Resource.success(response.body()))
                }else{
                    confirmedListLiveData.postValue(Resource.error("Response Error",null))
                }
            }

            override fun onFailure(call: Call<Enquiry>, t: Throwable) {
                confirmedListLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }

    private fun getErrorResult(
        response: Response<UpdateResult>,
        liveData: MutableLiveData<Resource<UpdateResult>>
    ) {
        try {
            val errorBody = response.errorBody()?.string().toString()
            val error = Gson().fromJson(errorBody, BaseResponse::class.java)
            liveData.postValue(Resource.error(error.message.toString(),null))
        }catch (e:Exception){
            liveData.postValue(Resource.error("No Response",null))
        }
    }

}