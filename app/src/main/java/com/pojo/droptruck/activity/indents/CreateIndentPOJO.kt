package com.pojo.droptruck.activity.indents

import com.google.gson.annotations.SerializedName

data class CreateIndentPOJO(

        /*customer_name:sales
        company_name:sales
        number_1:9344809554
        number_2:6385441765
        source_of_lead:Whatsapp
        pickup_location_id:1
        drop_location_id:2
        truck_type_id:1
        body_type:Open
        weight:100
        weight_unit:kg
        material_type_id:1
        pod_soft_hard_copy:soft
        remarks:no*/

        @SerializedName("customer_name")
        var customerName:String = "",
        @SerializedName("company_name")
        var companyName:String = "",
        @SerializedName("number_1")
        var numberOne:String = "",
        @SerializedName("number_2")
        var numberTwo:String="",
        @SerializedName("source_of_lead")
        var sourceLead:String="",
        @SerializedName("pickup_location_id")
        var pickUpLocation:String="",
        @SerializedName("drop_location_id")
        var dropLocation:String="",
        @SerializedName("truck_type_id")
        var truckType:String="",
        @SerializedName("body_type")
        var bodyType:String="",
        @SerializedName("weight")
        var weight:String="",
        @SerializedName("weight_unit")
        var weightUnit:String="",
        @SerializedName("material_type_id")
        var materialType:String="",
        @SerializedName("pod_soft_hard_copy")
        var podSoft:String="",
        @SerializedName("remarks")
        var remarks:String="",
        @SerializedName("payment_terms")
        var payTerms:String="",
        @SerializedName("user_id")
        var userId:String="",
        @SerializedName("user_type")
        var userType:String="",
        @SerializedName("indent_id")
        var indentId:String="",

        @SerializedName("new_material_type")
        var new_material_type:String="",
        @SerializedName("new_truck_type")
        var new_truck_type:String="",
        @SerializedName("new_body_type")
        var new_body_type:String="",
        @SerializedName("new_source_type")
        var new_source_type:String="",

        /*Material TYpe : new_material_type
        Truck Type: new_truck_type
        Body Type: new_body_type
        Source Type: new_source_type*/
        @SerializedName("pickup_city")
        var pickup_city:String="",
        @SerializedName("drop_city")
        var drop_city:String=""


)
