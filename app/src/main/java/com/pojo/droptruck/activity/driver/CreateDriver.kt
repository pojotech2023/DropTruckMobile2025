package com.pojo.droptruck.activity.driver

import com.google.gson.annotations.SerializedName

data class CreateDriver(
    @SerializedName("indent_id") var indentId:String="",
    @SerializedName("user_id") var userId:String="",
    @SerializedName("driver_name") var driverName:String="",
    @SerializedName("driver_number") var driverNumber:String="",
    @SerializedName("vehicle_number") var vehicleNumber:String="",
    @SerializedName("driver_base_location") var driverBaseLocation:String="",
    @SerializedName("vehicle_type") var vehicleType:String="",
    @SerializedName("truck_type") var truckType:String="",
    @SerializedName("vehicle_photo") var vehiclePhoto:String="",
    @SerializedName("rc_book") var rcBook:String="",
    @SerializedName("insurance") var insurance:String="",
    @SerializedName("driver_license") var driverLicense:String=""

)
