package com.pojo.droptruck.user.model

import com.google.gson.annotations.SerializedName

data class ProfileUpdate(

    /*customer_id:288
    customer_name:iniyaas
    contact_number:8877665544
    company_name:Truck company
    customer_email:iniya@gmail.com*/

    @SerializedName("customer_id")
    var customerId:String="",
    @SerializedName("customer_name")
    var customerName:String="",
    @SerializedName("contact_number")
    var contactNumber:String="",
    @SerializedName("company_name")
    var companyName:String="",
    @SerializedName("customer_email")
    var customerEmail:String=""

)
