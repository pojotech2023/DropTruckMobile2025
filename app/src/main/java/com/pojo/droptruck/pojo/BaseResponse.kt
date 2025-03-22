package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class BaseResponse(

    //@SerializedName("status"  ) var status  : Int = 0,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("details" ) var details : String?  = null,
    @SerializedName("error" ) var error : String?  = null
)
