package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class VersionResults(
    @SerializedName("status"  ) var status  : Int    = 0,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : VersionData  = VersionData()
)

data class VersionData(
    @SerializedName("version_code"  ) var vCode  : String  = "",
    @SerializedName("version_name" ) var vName : String = "",
)