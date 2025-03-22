package com.pojo.droptruck.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
@Parcelize
data class ConfirmIndent(
    @SerializedName("status"         ) var status        : Int?                   = null,
    @SerializedName("message"        ) var message       : String?                = null,
    @SerializedName("indent_details" ) var indentDetails : IndentDetails         = IndentDetails(),
    @SerializedName("indentRates"    ) var indentRates   : ArrayList<IndentRate> = arrayListOf()

):Parcelable
@Parcelize
data class IndentDetails (

    @SerializedName("id"              ) var id             : Int?    = null,
    @SerializedName("customer_name"   ) var customerName   : String? = null,
    @SerializedName("pickup_location" ) var pickupLocation : String? = null,
    @SerializedName("drop_location"   ) var dropLocation   : String? = null,
    @SerializedName("truck_type"      ) var truckType      : String? = null,
    @SerializedName("body_type"       ) var bodyType       : String? = null,
    @SerializedName("weight"          ) var weight         : String? = null,
    @SerializedName("weight_unit"     ) var weightUnit     : String? = null,
    @SerializedName("material_type"   ) var materialType   : String? = null,
    @SerializedName("sales_person"    ) var salesPerson    : String? = null,
    @SerializedName("customer_rate"    ) var customerRate  : String? = null,
    @SerializedName("driver_rate"    ) var driverRate      : String? = null

):Parcelable
@Parcelize
data class User (

    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("name"              ) var name            : String? = null,
    @SerializedName("email"             ) var email           : String? = null,
    @SerializedName("contact"           ) var contact         : String? = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt : String? = null,
    @SerializedName("designation"       ) var designation     : String? = null,
    @SerializedName("remarks"           ) var remarks         : String? = null,
    @SerializedName("status"            ) var status          : Int?    = null,
    @SerializedName("role_id"           ) var roleId          : Int?    = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null

):Parcelable
