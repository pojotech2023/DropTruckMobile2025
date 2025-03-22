package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class RegisterError(
    @SerializedName("status" ) var status : Int?   = null,
    @SerializedName("error"  ) var error  : Error? = Error()
)

data class Error (
    @SerializedName("contact_number" ) var contactNumber : ArrayList<String> = arrayListOf()
)