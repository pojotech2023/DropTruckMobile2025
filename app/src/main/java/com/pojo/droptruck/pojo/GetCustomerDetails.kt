package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName

data class GetCustomerDetails(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : CustomerDetails?   = CustomerDetails()
)

data class CustomerDetails (

    @SerializedName("id"                 ) var id               : Int?    = null,
    @SerializedName("customer_name"      ) var customerName     : String? = null,
    @SerializedName("company_name"       ) var companyName      : String? = null,
    @SerializedName("contact_number"     ) var contactNumber    : String? = null,
    @SerializedName("customer_email"     ) var customerEmail    : String? = null,
    @SerializedName("address"            ) var address          : String? = null,
    @SerializedName("gst_number"         ) var gstNumber        : String? = null,
    @SerializedName("lead_source"        ) var leadSource       : String? = null,
    @SerializedName("body_type"          ) var bodyType         : String? = null,
    @SerializedName("truck_type"         ) var truckType        : Int?    = null,
    @SerializedName("business_card"      ) var businessCard     : String? = null,
    @SerializedName("gst_document"       ) var gstDocument      : String? = null,
    @SerializedName("company_name_board" ) var companyNameBoard : String? = null,
    @SerializedName("remarks"            ) var remarks          : String? = null,
    @SerializedName("status"             ) var status           : Int?    = null,
    @SerializedName("find_our_app"       ) var findOurApp       : String? = null,
    @SerializedName("created_at"         ) var createdAt        : String? = null,
    @SerializedName("updated_at"         ) var updatedAt        : String? = null,
    @SerializedName("deleted_at"         ) var deletedAt        : String? = null

)