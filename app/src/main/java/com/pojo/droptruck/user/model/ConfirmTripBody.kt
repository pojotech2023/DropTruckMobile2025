package com.pojo.droptruck.user.model

import com.google.gson.annotations.SerializedName

data class ConfirmTripBody (

    @SerializedName("indent_id" ) var indentId : String? = null,
    @SerializedName("user_id"   ) var userId   : String? = null

)
