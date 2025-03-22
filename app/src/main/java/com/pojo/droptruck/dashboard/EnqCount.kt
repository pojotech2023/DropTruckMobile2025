package com.pojo.droptruck.dashboard

import com.google.gson.annotations.SerializedName

data class EnqCount(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Counts?   = Counts()
)

data class Counts(
    @SerializedName("quotedIndents"   ) var quotedIndents   : Int = 0,
    @SerializedName("unquotedIndents" ) var unquotedIndents : Int = 0
)