package com.pojo.droptruck.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pojo.droptruck.activity.indents.CreIndentRes
import com.pojo.droptruck.api.APIInterface
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.dashboard.EnqCount
import com.pojo.droptruck.pojo.BaseResponse
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.user.model.ProfileUpdate
import com.pojo.droptruck.user.model.UserCreateIndent
import com.pojo.droptruck.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CustomerApiRepository @Inject constructor(val api: APIInterface) {

    fun createIntent(
        docListLiveData: MutableLiveData<Resource<CreIndentRes>>,
        cInt: UserCreateIndent
    ) {

        api.customerCreateIntent(cInt).enqueue(object : Callback<CreIndentRes> {
            override fun onResponse(call: Call<CreIndentRes>, response: Response<CreIndentRes>) {
                if (response.isSuccessful){
                    docListLiveData.postValue(Resource.success(response.body()))
                }else{
                    docListLiveData.postValue(Resource.error("Response Error",null))
                }
            }

            override fun onFailure(call: Call<CreIndentRes>, t: Throwable) {
                docListLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

        //userBookingList
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


    fun callProfileAPI(
        docListLiveData: MutableLiveData<Resource<UpdateResult>>,
        cInt: ProfileUpdate) {

        api.customerProfileUpdate(cInt).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>, response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    docListLiveData.postValue(Resource.success(response.body()))
                }else{
                    docListLiveData.postValue(Resource.error("Response Error",null))
                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                docListLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }

    fun callHistoryAPI(
        docListLiveData: MutableLiveData<Resource<Enquiry>>,
        url: String, userId: String, page: String) {

        api.getHistoryList(url,userId, page).enqueue(object : Callback<Enquiry> {
            override fun onResponse(call: Call<Enquiry>,
                                    response: Response<Enquiry>) {

                if (response.isSuccessful){
                    docListLiveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        docListLiveData.postValue(Resource.error(error.details.toString(),null))
                    }catch (e:Exception){
                        docListLiveData.postValue(Resource.error("No Response",null))
                    }
                }
            }

            override fun onFailure(call: Call<Enquiry>, t: Throwable) {
                docListLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun callDelAccAPI(
        liveData: MutableLiveData<Resource<UpdateResult>>, value: String) {

        api.deleteAccount(value).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>, response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    getErrorResult(response,liveData)
                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }

    fun callGetCountAPI(liveData: MutableLiveData<Resource<EnqCount>>,url:String) {
        api.getCustomerEnqCount(url).enqueue(object : Callback<EnqCount> {
            override fun onResponse(call: Call<EnqCount>, response: Response<EnqCount>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }
            }

            override fun onFailure(call: Call<EnqCount>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun callSpinnerData(url: String): TypesPojo? {

        val res = api.spinnerAPICall(url).execute()

        if (res.isSuccessful) {
            return res.body()
        }else {
            val errorBody = res.errorBody()?.string().toString()
            val error = Gson().fromJson(errorBody, TypesPojo::class.java)

            return error
        }

    }


}