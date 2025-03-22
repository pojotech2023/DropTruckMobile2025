package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

class CustomerLoginError(
    @SerializedName("status" ) var status : Int?   = null,
    @SerializedName("message" ) var message : String?   = null,
    @SerializedName("error"  ) var error  : LoginError? = LoginError()
)
data class LoginError (
    @SerializedName("contact_number" ) var contactNumber : ArrayList<String> = arrayListOf()
)