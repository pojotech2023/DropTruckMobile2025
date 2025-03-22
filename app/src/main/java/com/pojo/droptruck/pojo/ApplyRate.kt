package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class ApplyRate(

    /*indent_id:219
    rate:20000
    name:shiny
    remarks:tesdfsdfsd*/

    @SerializedName("indent_id")
    var indentId:String = "",
    @SerializedName("rate")
    var rate:String = "",
    @SerializedName("name")
    var name:String = "",
    @SerializedName("remarks")
    var remarks: String = ""

)
