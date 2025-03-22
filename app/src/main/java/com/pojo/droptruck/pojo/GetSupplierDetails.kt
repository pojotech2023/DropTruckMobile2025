package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class GetSupplierDetails(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Suppliers?   = Suppliers()
)
