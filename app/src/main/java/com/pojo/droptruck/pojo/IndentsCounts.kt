package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class IndentsCounts(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : CountData?   = CountData()

)

data class CountData (

    @SerializedName("unquotedCount"         ) var unquotedCount         : Int? = null,
    @SerializedName("quotedCount"           ) var quotedCount           : Int? = null,
    @SerializedName("confirmedCount"        ) var confirmedCount        : Int? = null,
    @SerializedName("cancelledCount"        ) var CancelledCount        : Int? = null,
    @SerializedName("followupCount"         ) var followupCount         : Int? = null,
    @SerializedName("waitingForDriverCount" ) var waitingForDriverCount : Int? = null,
    @SerializedName("loadingCount"          ) var loadingCount          : Int? = null,
    @SerializedName("onTheRoadCount"        ) var onTheRoadCount        : Int? = null,
    @SerializedName("unloadingCount"        ) var unloadingCount        : Int? = null,
    @SerializedName("podCount"              ) var podCount              : Int? = null,
    @SerializedName("completedCount"        ) var completedCount        : Int? = null

)