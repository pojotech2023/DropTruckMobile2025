package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class LoginResults(
    @SerializedName("status"  ) var status  : Int    = 0,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = Data()
)

data class Data (
    @SerializedName("id"                ) var id              : Int    = 0,
    @SerializedName("name"              ) var name            : String? = null,
    @SerializedName("email"             ) var email           : String? = null,
    @SerializedName("contact"           ) var contact         : String? = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt : String? = null,
    @SerializedName("designation"       ) var designation     : String? = null,
    @SerializedName("remarks"           ) var remarks         : String? = null,
    @SerializedName("login_type"        ) var loginType       : String? = null,
    @SerializedName("status"            ) var status          : Int    = 0,
    @SerializedName("role_id"           ) var roleId          : Int?    = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null
)