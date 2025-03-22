package com.pojo.droptruck.activity.indents

import com.google.gson.annotations.SerializedName
import com.pojo.droptruck.pojo.Indents

data class CreIndentRes(
    @SerializedName("status"    ) var response    : Int? = null,
    @SerializedName("message"     ) var message     : String? = null,
    @SerializedName("data") var data  :  ArrayList<Indents> = arrayListOf()
)
