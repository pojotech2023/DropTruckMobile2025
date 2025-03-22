package com.pojo.droptruck.user.model

import com.google.gson.annotations.SerializedName

data class UserCreateIndent(

    /*pickup_location:Coimbatore
	drop_location:Erode
	truck_type:4
	body_type:Open
	weight:30
	weight_unit:tons
	user_id:34
	required_data:30-07-2024*/

    @SerializedName("pickup_location")
    var pickUpLocation:String="",
    @SerializedName("drop_location")
    var dropLocation:String="",
    @SerializedName("truck_type")
    var truckType:String="",
    @SerializedName("body_type")
    var bodyType:String="",
    @SerializedName("weight")
    var weight:String="",
    @SerializedName("weight_unit")
    var weightUnit:String="",
    @SerializedName("user_id")
    var userId:String="",
    @SerializedName("required_data")
    var reqDate:String="",
    @SerializedName("material_type_id")
    var matType:String="",

    @SerializedName("new_material_type")
    var new_material_type:String="",
    @SerializedName("new_truck_type")
    var new_truck_type:String="",
    @SerializedName("new_body_type")
    var new_body_type:String="",
    @SerializedName("new_source_type")
    var new_source_type:String="",

    @SerializedName("pickup_city")
    var pickup_city:String="",
    @SerializedName("drop_city")
    var drop_city:String=""



)
