package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class Login (
    @SerializedName("email"    ) var email    : String = "",
    @SerializedName("password" ) var password : String = ""
)