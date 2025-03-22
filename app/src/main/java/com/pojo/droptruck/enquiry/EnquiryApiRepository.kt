package com.pojo.droptruck.enquiry

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pojo.droptruck.api.APIInterface
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.BaseResponse
import com.pojo.droptruck.pojo.DriverDetails
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class EnquiryApiRepository @Inject constructor(val api: APIInterface) {

    fun callEnquiryApi(
        docListLiveData: MutableLiveData<Resource<Enquiry>>,
        loginType: String, userId: String,url:String,role: String, page: String) {

        api.getUnQuotedList(url,userId,page,role).enqueue(object : Callback<Enquiry> {
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

    fun callSupplierQuoteList(
        docListLiveData: MutableLiveData<Resource<Enquiry>>,
        url: String,userId: String, page: String) {

        api.getSupplierQuotedList(url,userId,page).enqueue(object : Callback<Enquiry> {
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

    fun callCreateRate(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        applyRate: ApplyRate,url:String) {

        api.createRate(applyRate,url).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>,
                                    response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    liveData.postValue(Resource.error("Response Error",null))
                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }

    fun callCancelIndentListApi(
        docListLiveData: MutableLiveData<Resource<Enquiry>>,
        url: String,userId: String, page: String) {

        api.getCancelledList(url,userId,page).enqueue(object : Callback<Enquiry> {
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

    fun callFollowUpListApi(
        docListLiveData: MutableLiveData<Resource<Enquiry>>, url: String,
        userId: String, page: String) {

        api.getFollowUpList(url,userId,page).enqueue(object : Callback<Enquiry> {
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

    fun callConfirmedListApi(
        docListLiveData: MutableLiveData<Resource<Enquiry>>,
        url: String,userId: String, page: String) {

        api.getConfirmedList(url,userId,page).enqueue(object : Callback<Enquiry> {
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

    fun callDeleteIndentApi(
        deleteLiveData: MutableLiveData<Resource<UpdateResult>>, url: String) {

        api.deleteIndent(url).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>,
                                    response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    deleteLiveData.postValue(Resource.success(response.body()))
                }else{
                    getErrorResult(response,deleteLiveData)
                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                deleteLiveData.postValue(Resource.error(t.message.toString(),null))
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

    fun callRestoreIndentAPI(
        resultLiveData: MutableLiveData<Resource<UpdateResult>>,
        id: Int, userId: String ) {
        api.restoreIndent(id.toString(),userId).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>,
                                    response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    resultLiveData.postValue(Resource.success(response.body()))
                }else{
                    getErrorResult(response,resultLiveData)
                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                resultLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun callDriverAPI(
        resultLiveData: MutableLiveData<Resource<DriverDetails>>,
        id: String, userId: String ) {
        api.driverDetails(id.toString(),userId).enqueue(object : Callback<DriverDetails> {
            override fun onResponse(call: Call<DriverDetails>,
                                    response: Response<DriverDetails>) {
                if (response.isSuccessful){
                    resultLiveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        resultLiveData.postValue(Resource.error(error.message.toString(),null))
                    }catch (e:Exception){
                        resultLiveData.postValue(Resource.error("No Response",null))
                    }
                }
            }

            override fun onFailure(call: Call<DriverDetails>, t: Throwable) {
                resultLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun callCloneIndentAPI(resultLiveData: MutableLiveData<Resource<UpdateResult>>, indentId: String) {
        api.cloneIndent(indentId).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>,
                                    response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    resultLiveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        resultLiveData.postValue(Resource.error(error.message.toString(),null))
                    }catch (e:Exception){
                        resultLiveData.postValue(Resource.error("No Response",null))
                    }
                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                resultLiveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

}