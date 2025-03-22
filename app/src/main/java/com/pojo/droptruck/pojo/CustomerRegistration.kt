package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class CustomerRegistration (

    @SerializedName("customer_name"  ) var customerName  : String? = null,
    @SerializedName("contact_number" ) var contactNumber : String? = null,
    @SerializedName("company_name"   ) var companyName   : String? = null,
    @SerializedName("customer_email" ) var customerEmail : String? = null,
    @SerializedName("find_our_app"   ) var findOurApp    : String? = null

)
