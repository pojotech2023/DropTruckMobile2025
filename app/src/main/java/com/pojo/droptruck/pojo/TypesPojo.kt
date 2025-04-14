package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class TypesPojo (
    @SerializedName("status"  ) var status  : Int?            = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<SpinnerData> = arrayListOf(),

    //base Response For Error...
    @SerializedName("details" ) var details : String?  = null,
    @SerializedName("error" ) var error : String?  = null

)

data class SpinnerData (

    @SerializedName("id"   ) var id   : Int?    = null,
    @SerializedName("name" ) var name : String? = null


){
    override fun toString(): String {
        return name.toString()
    }
}