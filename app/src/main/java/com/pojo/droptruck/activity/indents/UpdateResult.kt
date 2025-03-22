package com.pojo.droptruck.activity.indents

import com.google.gson.annotations.SerializedName

data class UpdateResult(
    @SerializedName("status"    ) var response    : Int? = null,
    @SerializedName("message"     ) var message     : String? = null,
    @SerializedName("selectedIndentId") var indentId  : String? = null,
)
