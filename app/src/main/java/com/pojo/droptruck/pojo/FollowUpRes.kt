package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class FollowUpRes(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : FollowUpData?   = FollowUpData()
)

data class FollowUpData (
    @SerializedName("today"    ) var today    : Int? = null,
    @SerializedName("tomorrow" ) var tomorrow : Int? = null
)
