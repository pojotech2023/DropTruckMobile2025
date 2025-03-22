package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class CustomerLogin(
@SerializedName("status"    ) var response    : Int? = null,
@SerializedName("message"     ) var message     : String? = null,
@SerializedName("otp"     ) var otp     : String? = null,
)
