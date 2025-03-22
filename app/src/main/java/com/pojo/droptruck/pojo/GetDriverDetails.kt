package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class GetDriverDetails(
@SerializedName("status"  ) var status  : Int?    = null,
@SerializedName("message" ) var message : String? = null,
@SerializedName("data"    ) var data    : Driver?   = Driver()
)
