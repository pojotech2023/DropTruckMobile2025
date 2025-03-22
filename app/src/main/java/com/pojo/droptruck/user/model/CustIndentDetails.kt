package com.pojo.droptruck.user.model

import com.google.gson.annotations.SerializedName
import com.pojo.droptruck.pojo.Indents

data class CustIndentDetails(
    @SerializedName("status"         ) var status        : Int?                   = null,
    @SerializedName("message"        ) var message       : String?                = null,
    @SerializedName("data"           ) var data          : ArrayList<Indents> = arrayListOf()
    //@SerializedName("data"           ) var data          : Indents = Indents()
)
