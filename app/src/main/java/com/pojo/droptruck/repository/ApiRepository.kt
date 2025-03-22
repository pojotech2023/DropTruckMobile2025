package com.pojo.droptruck.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pojo.droptruck.activity.driver.CreateDriver
import com.pojo.droptruck.api.APIInterface
import com.pojo.droptruck.activity.indents.CreateIndentPOJO
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.pojo.BaseResponse
import com.pojo.droptruck.pojo.ConfirmIndent
import com.pojo.droptruck.pojo.CustomerLogin
import com.pojo.droptruck.pojo.CustomerRegistration
import com.pojo.droptruck.pojo.GetCustomerDetails
import com.pojo.droptruck.pojo.GetDriverDetails
import com.pojo.droptruck.pojo.GetSupplierDetails
import com.pojo.droptruck.pojo.IndentsCounts
import com.pojo.droptruck.pojo.Login
import com.pojo.droptruck.pojo.LoginResults
import com.pojo.droptruck.pojo.RegisterError
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.pojo.VersionResults
import com.pojo.droptruck.user.model.CustIndentDetails
import com.pojo.droptruck.utils.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(val api: APIInterface) {

    fun createIntent(
        docListLiveData: MutableLiveData<Resource<UpdateResult>>,
        cInt: CreateIndentPOJO
    ) {

        api.createIntent(cInt).enqueue(object : Callback<UpdateResult> {
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

        //userBookingList
    }

    fun confirmIndent(liveData: MutableLiveData<Resource<ConfirmIndent>>,
                      url: String) {

        api.getConfirmIndentDetails(url).enqueue(object : Callback<ConfirmIndent> {
            override fun onResponse(call: Call<ConfirmIndent>, response: Response<ConfirmIndent>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {

                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, ConfirmIndent::class.java)
                        //liveData.postValue(Resource.error(error.message.toString(),null))

                        liveData.postValue(Resource.success(error))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Response Error",null))
                    }
                }
            }

            override fun onFailure(call: Call<ConfirmIndent>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }

    fun callLogin(user: Login, liveData: MutableLiveData<Resource<LoginResults>>) {
        api.callLogin(user).enqueue(object : Callback<LoginResults> {
            override fun onResponse(call: Call<LoginResults>, response: Response<LoginResults>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.message.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error(e.message.toString(),null))
                    }
                }
            }

            override fun onFailure(call: Call<LoginResults>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun confirmDriverAmount(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        indentId: String, rateId: String, userId: String) {

        api.confirmDriverAmount(indentId,rateId,userId).enqueue(object : Callback<UpdateResult> {
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

    fun customerRateUpdate(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        roleId:String,indentId: String, amount: String, userId: String) {

        api.customerRateUpdate(roleId,indentId,amount,userId).enqueue(object : Callback<UpdateResult> {
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

    fun deleteRateAPI(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        indentId: String,userId: String) {

        api.deleteDriveAmt(indentId,userId).enqueue(object : Callback<UpdateResult> {
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

    fun confirmTripAPI(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        indentId: String,userId: String) {

        api.confirmTrip(indentId,userId).enqueue(object : Callback<UpdateResult> {
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

    fun customerConfirmTripAPI(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        indentId: String,userId: String) {

        api.customerConfirmTrip(indentId,userId).enqueue(object : Callback<UpdateResult> {
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

    fun cancelTripAPI(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        indentId: String,userId: String,cancelReason:String,reason:String,date:String,fReason:String) {

        api.cancelTrip(indentId,userId,cancelReason,reason,date,fReason).enqueue(object : Callback<UpdateResult> {
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

    fun customerCancelTripAPI(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        indentId: String,userId: String,cancelReason:String,reason:String,date:String,fReason:String) {

        api.customerCancelTrip(indentId,userId,cancelReason,reason,date,fReason).enqueue(object : Callback<UpdateResult> {
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

    fun createDriverAPICall(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        createDriver: CreateDriver) {
        api.createDriver(createDriver).enqueue(object : Callback<UpdateResult> {
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

    fun callCustomerRegistration(reg: CustomerRegistration, liveData: MutableLiveData<Resource<UpdateResult>>) {
        api.customerSignup(reg).enqueue(object : Callback<UpdateResult> {
            override fun onResponse(call: Call<UpdateResult>, response: Response<UpdateResult>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, RegisterError::class.java)

                        if (error.error!=null && error.error?.contactNumber!=null &&
                            error.error?.contactNumber!!.size >0) {
                            liveData.postValue(Resource.error(
                                error.error?.contactNumber!![0],null))
                        }else {
                            liveData.postValue(Resource.error("Error",null))
                        }


                    }catch (e:Exception){
                        liveData.postValue(Resource.error(e.message.toString(),null))
                    }

                }
            }

            override fun onFailure(call: Call<UpdateResult>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    //fun customerLogin(number: String, liveData: MutableLiveData<Resource<UpdateResult>>) {
    fun customerLogin(number: String, liveData: MutableLiveData<Resource<CustomerLogin>>) {
        api.customerLogin(number).enqueue(object : Callback<CustomerLogin> {
            override fun onResponse(call: Call<CustomerLogin>, response: Response<CustomerLogin>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, CustomerLogin::class.java)
                        liveData.postValue(Resource.success(error))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Kindly Signup as a New User",null))
                    }
                }
            }

            override fun onFailure(call: Call<CustomerLogin>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun otpVerification(otp: String, liveData: MutableLiveData<Resource<LoginResults>>,
                        mobile:String) {
        api.otpVerification(mobile,otp).enqueue(object : Callback<LoginResults> {
            override fun onResponse(call: Call<LoginResults>, response: Response<LoginResults>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.message.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error(e.message.toString(),null))
                    }

                }
            }

            override fun onFailure(call: Call<LoginResults>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun callVersionCheck(liveData: MutableLiveData<Resource<VersionResults>>) {
        api.getVersionDetails().enqueue(object : Callback<VersionResults> {
            override fun onResponse(call: Call<VersionResults>, response: Response<VersionResults>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.message.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error(e.message.toString(),null))
                    }

                }
            }

            override fun onFailure(call: Call<VersionResults>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun createDriverAPICall(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        rbIndentId: RequestBody, rbUserId: RequestBody,
        rbDriverName: RequestBody, rbDriverNumber: RequestBody,
        rbVehicleNumber: RequestBody, rbBaseLocation: RequestBody,
        rbTruckType: RequestBody, rbVehicleType: RequestBody,
        vehiclePhoto: MultipartBody.Part?, rcbPhoto: MultipartBody.Part?,
        dlPhoto: MultipartBody.Part?, insPhoto: MultipartBody.Part?) {

        api.createDriver(rbIndentId,rbUserId,rbDriverName,rbDriverNumber,rbVehicleNumber,
            rbBaseLocation,rbTruckType,rbVehicleType,vehiclePhoto,rcbPhoto,dlPhoto,insPhoto).enqueue(object : Callback<UpdateResult> {
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

    fun createSupplierAPICall(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        rbIndentId: RequestBody, rbUserId: RequestBody,
        rbVendor: RequestBody, rbSupplierType: RequestBody,
        rbCompany: RequestBody, rbContact: RequestBody,
        rbPanNumber: RequestBody, rbBankName: RequestBody,
        rbIfscCode: RequestBody, rbAccNum: RequestBody, rbConfirmAccNum: RequestBody,
        rbRemarks: RequestBody,
        panCard: MultipartBody.Part?, businessCard: MultipartBody.Part?,
        memo: MultipartBody.Part?, bankDetails: MultipartBody.Part?,
        ewayBill: MultipartBody.Part?, tripsInvoices: MultipartBody.Part?,
        otherDocument: MultipartBody.Part?
    ) {

        api.createSupplier( rbIndentId,rbVendor,rbSupplierType,rbCompany,rbContact,rbPanNumber,
            rbBankName,rbIfscCode,rbAccNum,rbConfirmAccNum,rbRemarks,
            panCard,businessCard,memo,bankDetails,ewayBill,tripsInvoices,otherDocument
        ).enqueue(object : Callback<UpdateResult> {
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

    fun createExtraCostAPICall(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        rbIndentId: RequestBody,
        rbUserId: RequestBody,
        rbAmount: RequestBody,
        rbCostType: RequestBody,
        billCopy: MultipartBody.Part?,
        unloadingPhoto: MultipartBody.Part?,
        billCopiesInfo: MultipartBody.Part?) {

        api.createExtraCost(rbIndentId,rbUserId,rbCostType,rbAmount,billCopy,unloadingPhoto,
            billCopiesInfo
        ).enqueue(object : Callback<UpdateResult> {
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

    fun createExtraCostAPICall(
        liveData: MutableLiveData<Resource<UpdateResult>>,
        rbIndentId: RequestBody,
        rbUserId: RequestBody,
        rbAmount: RequestBody,
        rbCostType: RequestBody) {

        api.createExtraCost(rbIndentId,rbUserId,rbCostType,rbAmount).enqueue(object : Callback<UpdateResult> {
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

    fun podWithOutDataAPI(
        indentId: String, userId: String, s: String,
        liveData: MutableLiveData<Resource<UpdateResult>>) {

        api.submitPOD(indentId,userId,s).enqueue(object : Callback<UpdateResult> {
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

    fun podWithDataAPI(
        indentId: RequestBody,
        userId: RequestBody,
        s: RequestBody,
        courierNumber: RequestBody,
        podSoftCopy: MultipartBody.Part?,
        podCourier: MultipartBody.Part?,
        liveData: MutableLiveData<Resource<UpdateResult>>,
        pod_courier_photo: MultipartBody.Part?
    ) {

        api.submitPODWithData(indentId,userId,s,courierNumber,
            podSoftCopy,podCourier,pod_courier_photo).enqueue(object : Callback<UpdateResult> {
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

    fun getDriverDetailsAPI(
        vehicleNum: String,
        liveData: MutableLiveData<Resource<GetDriverDetails>>) {

        api.getDriverDetails(vehicleNum).enqueue(object : Callback<GetDriverDetails> {
            override fun onResponse(call: Call<GetDriverDetails>, response: Response<GetDriverDetails>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.error.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Error Driver Details Not Found",null))
                    }
                }
            }

            override fun onFailure(call: Call<GetDriverDetails>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun getSupplierDetailsAPI(
        vehicleNum: String,
        liveData: MutableLiveData<Resource<GetSupplierDetails>>) {

        api.getSupplierDetails(vehicleNum).enqueue(object : Callback<GetSupplierDetails> {
            override fun onResponse(call: Call<GetSupplierDetails>, response: Response<GetSupplierDetails>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.error.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Error Supplier Details Not Found",null))
                    }
                }
            }

            override fun onFailure(call: Call<GetSupplierDetails>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun getCustomerDetailsAPI(
        vehicleNum: String,
        liveData: MutableLiveData<Resource<GetCustomerDetails>>) {

        api.getCustomerDetails(vehicleNum).enqueue(object : Callback<GetCustomerDetails> {
            override fun onResponse(call: Call<GetCustomerDetails>, response: Response<GetCustomerDetails>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.error.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Error Customer Details Not Found",null))
                    }
                }
            }

            override fun onFailure(call: Call<GetCustomerDetails>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun getIndentCounts(liveData: MutableLiveData<Resource<IndentsCounts>>,
                      url: String) {

        api.getCounts(url).enqueue(object : Callback<IndentsCounts> {
            override fun onResponse(call: Call<IndentsCounts>, response: Response<IndentsCounts>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, IndentsCounts::class.java)
                        //liveData.postValue(Resource.success(error))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Response Error",null))
                    }
                }
            }

            override fun onFailure(call: Call<IndentsCounts>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }

    fun customerIndentDetails(liveData: MutableLiveData<Resource<CustIndentDetails>>, url: String) {

        api.getCustomerSideIndentDetails(url).enqueue(object : Callback<CustIndentDetails> {
            override fun onResponse(call: Call<CustIndentDetails>, response: Response<CustIndentDetails>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, CustIndentDetails::class.java)
                        liveData.postValue(Resource.success(error))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("Response Error",null))
                    }
                }
            }

            override fun onFailure(call: Call<CustIndentDetails>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })

    }


    fun callUserStatusCheck(liveData: MutableLiveData<Resource<LoginResults>>,url:String) {
        api.checkStatus(url).enqueue(object : Callback<LoginResults> {
            override fun onResponse(call: Call<LoginResults>, response: Response<LoginResults>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, LoginResults::class.java)
                        liveData.postValue(Resource.success(error))
                    }catch (e:Exception){
                        Log.d("Exception",e.message.toString())
                        liveData.postValue(Resource.error(e.message.toString(),null))
                    }
                }
            }

            override fun onFailure(call: Call<LoginResults>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun updateFCMToken(url: String, userId: String, role: String, fcm: String) {
        api.fcmTokenUpdate(url,userId,role,fcm).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    Log.d("FCM Result","Success")
                }else{
                    Log.d("FCM Result","Failure")
                }

            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("FCM Result","Failure "+t.message)
            }

        })
    }

    fun callSpinnerData(liveData: MutableLiveData<Resource<TypesPojo>>,url:String) {
        api.spinnerAPICall(url).enqueue(object : Callback<TypesPojo> {
            override fun onResponse(call: Call<TypesPojo>, response: Response<TypesPojo>) {
                if (response.isSuccessful){
                    liveData.postValue(Resource.success(response.body()))
                }else{
                    try {
                        val errorBody = response.errorBody()?.string().toString()
                        val error = Gson().fromJson(errorBody, BaseResponse::class.java)
                        liveData.postValue(Resource.error(error.details.toString(),null))
                    }catch (e:Exception){
                        liveData.postValue(Resource.error("No Response",null))
                    }
                }
            }

            override fun onFailure(call: Call<TypesPojo>, t: Throwable) {
                liveData.postValue(Resource.error(t.message.toString(),null))
            }

        })
    }

    fun callSpinnerData(url: String):TypesPojo? {

        val res = api.spinnerAPICall(url).execute()

        if (res.isSuccessful) {
            return res.body()
            /*return MutableLiveData<TypesPojo>().apply {
                Log.d("Value::: ",Gson().toJson(res.body()))
                postValue(res.body())
            }*/
        }else {
            val errorBody = res.errorBody()?.string().toString()
            val error = Gson().fromJson(errorBody, TypesPojo::class.java)

            return error
            /*MutableLiveData<TypesPojo>().apply {
                postValue(null)
            */}

        }
    }


//}