package com.pojo.droptruck.api

import com.pojo.droptruck.activity.driver.CreateDriver
import com.pojo.droptruck.activity.indents.CreIndentRes
import com.pojo.droptruck.activity.indents.CreateIndentPOJO
import com.pojo.droptruck.activity.indents.UpdateResult
import com.pojo.droptruck.dashboard.EnqCount
import com.pojo.droptruck.pojo.ApplyRate
import com.pojo.droptruck.pojo.BaseResponse
import com.pojo.droptruck.pojo.ConfirmIndent
import com.pojo.droptruck.pojo.CustomerLogin
import com.pojo.droptruck.pojo.CustomerRegistration
import com.pojo.droptruck.pojo.DriverDetails
import com.pojo.droptruck.pojo.Enquiry
import com.pojo.droptruck.pojo.FollowUpData
import com.pojo.droptruck.pojo.FollowUpRes
import com.pojo.droptruck.pojo.GetCustomerDetails
import com.pojo.droptruck.pojo.GetDriverDetails
import com.pojo.droptruck.pojo.GetSupplierDetails
import com.pojo.droptruck.pojo.IndentsCounts
import com.pojo.droptruck.pojo.Login
import com.pojo.droptruck.pojo.LoginResults
import com.pojo.droptruck.pojo.TypesPojo
import com.pojo.droptruck.pojo.VersionResults
import com.pojo.droptruck.user.model.CustIndentDetails
import com.pojo.droptruck.user.model.ProfileUpdate
import com.pojo.droptruck.user.model.UserCreateIndent
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface APIInterface {

    //@POST("indents/store/28")
    @POST("sales/store")
    fun createIntent(@Body cIntent: CreateIndentPOJO): Call<UpdateResult>

    //https://droptruck.pojotech.in/api/api/sales/indents-list/3/28
    //@GET("sales/indents-list/3/28") // unquoted
    @FormUrlEncoded
    @POST
    fun getUnQuotedList(@Url url:String,@Field("user_id") user_id: String,@Field("page") page: String
                        ,@Field("user_type") user_type: String): Call<Enquiry>

    //https://droptruck.pojotech.in/api/api/sales/quoted/29
    //@GET // supplier quoted
    @FormUrlEncoded
    @POST
    fun getSupplierQuotedList(@Url url:String, @Field("user_id") user_id: String,
                              @Field("page") page: String): Call<Enquiry>

    //https://droptruck.pojotech.in/api/api/rates/store/3
    @POST //("rates/store/3")
    fun createRate(@Body rate: ApplyRate,@Url url:String): Call<UpdateResult>

    //https://droptruck.pojotech.in/api/api/sales/confirm-indent-details/28/1080
    @GET
    fun getConfirmIndentDetails(@Url url:String): Call<ConfirmIndent>

    @POST("user-login")
    fun callLogin(@Body user: Login): Call<LoginResults>

    @DELETE
    fun deleteIndent(@Url url:String): Call<UpdateResult>

    @FormUrlEncoded
    @POST("sales/confirm-driver-amount")
    fun confirmDriverAmount(@Field("indent_id") indentId: String,
                            @Field("rate_id") rate_id: String,
                            @Field("user_id") user_id: String): Call<UpdateResult>

    @FormUrlEncoded
    @POST("sales/customer-rate-update/{role_id}")
    fun customerRateUpdate(@Path("role_id") role_id:String,@Field("indent_id") indentId: String,
                           @Field("rate") rate: String,
                           @Field("user_id") user_id: String): Call<UpdateResult>

    @FormUrlEncoded
    @POST("sales/delete-driver-amount")
    fun deleteDriveAmt(@Field("indent_id") indentId: String,@Field("user_id") user_id: String): Call<UpdateResult>

    @FormUrlEncoded
    @POST("sales/confirm-to-trips")
    fun confirmTrip(@Field("indent_id") indentId: String,@Field("user_id") user_id: String): Call<UpdateResult>

    // sales cancelled list
    @FormUrlEncoded
    @POST
    fun getCancelledList(@Url url:String,@Field("user_id") user_id: String,
                         @Field("page") page: String): Call<Enquiry>

    //@GET // sales followup list
    @FormUrlEncoded
    @POST
    fun getFollowUpList(@Url url:String,@Field("user_id") user_id: String,
                        @Field("page") page: String): Call<Enquiry>

    //@GET // sales confirmed list
    @FormUrlEncoded
    @POST
    fun getConfirmedList(@Url url:String,@Field("user_id") user_id: String,
                         @Field("page") page: String): Call<Enquiry>

    //https://droptruck.pojotech.in/api/api/sales/cancel-indent
    @FormUrlEncoded
    @POST("sales/cancel-indent")
    fun cancelTrip(@Field("indent_id") indentId: String,@Field("user_id") user_id: String,
                    @Field("cancel_reason") cancelReason: String, @Field("reason") reason: String,
                    @Field("followup_date") date:String,@Field("followup_reason") fReason:String):
            Call<UpdateResult>

    //@GET
    @FormUrlEncoded
    @POST
    fun getTripsList(@Url url:String,@Field("user_id") user_id: String,
                     @Field("page") page: String): Call<Enquiry>

    //https://droptruck.pojotech.in/api/api/trips/supplier/create-driver
    @Multipart
    @POST("trips/supplier/create-driver")
    fun createDriver(@Part("indent_id") indentId: RequestBody, @Part("user_id") userId: RequestBody,
                     @Part("driver_name") driverName: RequestBody, @Part("driver_number") driverNumber: RequestBody,
                     @Part("vehicle_number") vehicleNumber:RequestBody,
                     @Part("driver_base_location") baseLocation:RequestBody,
                     @Part("truck_type") truckType:RequestBody,
                     @Part("vehicle_type") vehicleType:RequestBody,
                     @Part vehiclePhoto: MultipartBody.Part?,
                     @Part rcBook: MultipartBody.Part?,
                     @Part insurance: MultipartBody.Part?,
                     @Part driverLicense: MultipartBody.Part?): Call<UpdateResult>

    @Multipart
    @POST("trips/supplier/create-supplier")
    fun createSupplier(@Part("indent_id") indentId: RequestBody,
                     @Part("supplier_name") supplierName: RequestBody, @Part("supplier_type") supplierType: RequestBody,
                     @Part("company_name") companyName:RequestBody, @Part("contact_number") contactNumber:RequestBody,
                     @Part("pan_card_number") panCardNumber:RequestBody, @Part("bank_name") bankName:RequestBody,
                     @Part("ifsc_code") ifscCode:RequestBody, @Part("account_number") accountNumber:RequestBody,
                     @Part("re_account_number") reAccountNumber:RequestBody, @Part("remarks") remarks:RequestBody,
                     @Part panCard: MultipartBody.Part?, @Part businessCard: MultipartBody.Part?,
                     @Part memo: MultipartBody.Part?, @Part bankDetails: MultipartBody.Part?,
                     @Part ewayBill: MultipartBody.Part?, @Part tripsInvoices: MultipartBody.Part?,
                     @Part otherDocument: MultipartBody.Part?): Call<UpdateResult>

    //droptruck.pojotech.in/api/api/trips/supplier/create-extracost
    @Multipart
    @POST("trips/supplier/create-extracost")
    fun createExtraCost(@Part("indent_id") indentId: RequestBody, @Part("user_id") userId: RequestBody,
                     @Part("extra_cost_type") costType: RequestBody, @Part("amount") amount: RequestBody,
                     @Part billCopy: MultipartBody.Part?,
                     @Part unloadingPhoto: MultipartBody.Part?,
                     @Part billCopiesInfo: MultipartBody.Part?): Call<UpdateResult>
    @Multipart
    @POST("trips/supplier/create-extracost")
    fun createExtraCost(@Part("indent_id") indentId: RequestBody, @Part("user_id") userId: RequestBody,
                        @Part("extra_cost_type") costType: RequestBody,
                        @Part("amount") amount: RequestBody): Call<UpdateResult>

    //trips/supplier/create-pod
    @Multipart
    @POST("trips/supplier/create-pod")
    fun createPOD(@Part("indent_id") indentId: RequestBody, @Part("user_id") userId: RequestBody,
                        @Part("submit_with_data") submit_with_data: RequestBody,
                        @Part("courier_receipt_no") courier_receipt_no: RequestBody,
                        @Part pod_soft_copy: MultipartBody.Part?,
                        @Part pod_courier: MultipartBody.Part?): Call<UpdateResult>


    //@Part("profile_image") profile_image: RequestBody?

    //Customer....
    @POST("customer/signup")
    fun customerSignup(@Body reg: CustomerRegistration): Call<UpdateResult>

    @FormUrlEncoded
    @POST("customer/login")
    fun customerLogin(@Field("contact_number") contactNumber: String): Call<CustomerLogin>

    @FormUrlEncoded
    //https://droptruck.pojotech.in/api/api/customer/verify-otp
    @POST("customer/verify-otp")
    fun otpVerification(@Field("contact_number") num: String,@Field("otp") otp: String): Call<LoginResults>

    @POST("customer/create-indent")
    fun customerCreateIntent(@Body cIntent: UserCreateIndent): Call<CreIndentRes>

    @POST("customer/profile-update")
    fun customerProfileUpdate(@Body profile: ProfileUpdate): Call<UpdateResult>

    @FormUrlEncoded
    @POST
    fun getHistoryList(@Url url:String,@Field("user_id") user_id: String,
                       @Field("page") page: String): Call<Enquiry>

    @FormUrlEncoded
    @POST("customer/confirm-trips")
    fun customerConfirmTrip(@Field("indent_id") indentId: String,@Field("user_id") user_id: String): Call<UpdateResult>

    @FormUrlEncoded
    @POST("customer/cancel-indent-details")
    fun customerCancelTrip(@Field("indent_id") indentId: String,@Field("user_id") user_id: String,
                   @Field("cancel_reason") cancelReason: String, @Field("reason") reason: String,
                   @Field("followup_date") date:String,@Field("followup_reason") fReason:String):
            Call<UpdateResult>

    @FormUrlEncoded
    @POST("delete-account")
    fun deleteAccount(@Field("contact_number_email") value: String): Call<UpdateResult>

    //sales/restore-indent
    @FormUrlEncoded
    @POST("sales/restore-indent")
    fun restoreIndent(@Field("indent_id") indentId: String,@Field("user_id") user_id: String): Call<UpdateResult>

    @GET("versions")
    fun getVersionDetails(): Call<VersionResults>

    //trips/sales/driver-details
    @FormUrlEncoded
    @POST("trips/sales/driver-details")
    fun driverDetails(@Field("indent_id") indentId: String,@Field("user_id") user_id: String): Call<DriverDetails>

    @GET
    fun getCustomerEnqCount(@Url url:String): Call<EnqCount>

    @POST("trips/supplier/create-driver")
    fun createDriver(@Body createDriver: CreateDriver): Call<UpdateResult>

    @FormUrlEncoded
    @POST("trips/supplier/create-pod")
    fun submitPOD(@Field("indent_id") indentId: String,
                  @Field("user_id") userId: String,
                  @Field("pod_type") s: String): Call<UpdateResult>

    @Multipart
    @POST("trips/supplier/create-pod")
    fun submitPODWithData(@Part("indent_id") indentId: RequestBody, @Part("user_id") userId: RequestBody,
                  @Part("pod_type") submit_with_data: RequestBody,
                  @Part("courier_receipt_no") courier_receipt_no: RequestBody,
                  @Part pod_soft_copy: MultipartBody.Part?,
                  @Part pod_courier: MultipartBody.Part?,
                  @Part pod_courier_photo: MultipartBody.Part?): Call<UpdateResult>

    //https://droptruck.pojotech.in/api/api/suppliers/get-supplier-details
    @FormUrlEncoded
    @POST("suppliers/get-supplier-details")
    fun getSupplierDetails(@Field("mobile_number") mobileNumber: String): Call<GetSupplierDetails>

    @FormUrlEncoded
    @POST("suppliers/get-customer-details")
    fun getCustomerDetails(@Field("mobile_number") mobileNumber: String): Call<GetCustomerDetails>

    //https://droptruck.pojotech.in/api/api/suppliers/get-driver-details
    @FormUrlEncoded
    @POST("suppliers/get-driver-details")
    fun getDriverDetails(@Field("vehicle_number") vehicleNumber: String): Call<GetDriverDetails>

    @GET
    fun getCounts(@Url url:String): Call<IndentsCounts>

    @GET
    fun getCustomerSideIndentDetails(@Url url:String): Call<CustIndentDetails>

    @GET
    fun checkStatus(@Url url:String): Call<LoginResults>

    @FormUrlEncoded
    @POST
    fun fcmTokenUpdate(@Url url: String,@Field("user_id") userId: String,
                       @Field("user_type")role: String, @Field("device_token")fcm: String): Call<BaseResponse>

    @GET
    fun spinnerAPICall(@Url url:String): Call<TypesPojo>

    @FormUrlEncoded
    @POST("clone-indents")
    fun cloneIndent(@Field("indent_id") userId: String): Call<UpdateResult>

    @GET("sales/followup-count") //sales/followup-count?user_id=3
    fun followUpCount(@Query("user_id") userId:String): Call<FollowUpRes>

}