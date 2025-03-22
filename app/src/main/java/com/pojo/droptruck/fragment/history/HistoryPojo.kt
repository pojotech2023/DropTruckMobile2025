package com.pojo.droptruck.fragment.history

import com.google.gson.annotations.SerializedName
import com.pojo.droptruck.pojo.Indents

data class HistoryPojo(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = Data()
)

data class Data (

    @SerializedName("canceledIndents" ) var canceledIndents : ArrayList<Indents> = arrayListOf(),
    @SerializedName("completedTrips"  ) var completedTrips  : ArrayList<Indents> = arrayListOf()

)