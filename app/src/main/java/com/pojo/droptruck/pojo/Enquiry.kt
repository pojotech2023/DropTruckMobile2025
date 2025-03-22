package com.pojo.droptruck.pojo

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Enquiry(
    @SerializedName("status"           ) var status           : Int?               = null,
    @SerializedName("indents"          ) var indents          : ArrayList<Indents> = arrayListOf(),
    @SerializedName("indentCount"      ) var indentCount      : Int?               = null,
    @SerializedName("selectedIndentId" ) var selectedIndentId : Int?               = null,
    //@SerializedName("weightUnits"      ) var weightUnits      : WeightUnits?       = WeightUnits(),
    //@SerializedName("materialTypes"    ) var materialTypes    : ArrayList<String>  = arrayListOf(),
    //@SerializedName("truckTypes"       ) var truckTypes       : ArrayList<String>  = arrayListOf(),
    //@SerializedName("locations"        ) var locations        : ArrayList<String>  = arrayListOf()

    //cancel...
    @SerializedName("data"          ) var dataIndents          : ArrayList<Indents> = arrayListOf(),

    @SerializedName("current_page" ) var currentPage : String? = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?    = null,
    @SerializedName("total_count"  ) var totalCount  : Int?    = null,
    @SerializedName("per_page"     ) var perPage     : Int?    = null

): Parcelable
@Parcelize
data class IndentRate (
    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("indent_id"         ) var indentId        : Int?    = null,
    @SerializedName("name"              ) var name            : String? = null,
    @SerializedName("rate"              ) var rate            : String? = null,
    @SerializedName("is_confirmed_rate" ) var isConfirmedRate : Int?    = null,
    @SerializedName("remarks"           ) var remarks         : String? = null,
    @SerializedName("user_id"           ) var userId          : Int?    = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null,
    @SerializedName("user"              ) var user            : User?   = User()
    ): Parcelable
@Parcelize
data class Indents (
    @SerializedName("required_date" ) var reqDate               : String?               = "",
    @SerializedName("id"                 ) var id               : Int                  = 0,
    @SerializedName("customer_name"      ) var customerName     : String?               = null,
    @SerializedName("company_name"       ) var companyName      : String?               = null,
    @SerializedName("number_1"           ) var number1          : String?               = null,
    @SerializedName("number_2"           ) var number2          : String?               = null,
    @SerializedName("source_of_lead"     ) var sourceOfLead     : String?               = null,
    @SerializedName("pickup_location_id" ) var pickupLocationId : String?               = null,
    @SerializedName("drop_location_id"   ) var dropLocationId   : String?               = null,
    @SerializedName("body_type"          ) var bodyType         : String?               = null,
    @SerializedName("weight"             ) var weight           : String?               = null,
    @SerializedName("weight_unit"        ) var weightUnit       : String?               = null,
    @SerializedName("pod_soft_hard_copy" ) var podSoftHardCopy  : String?               = null,
    @SerializedName("payment_terms"      ) var paymentTerms     : String?               = null,
    @SerializedName("remarks"            ) var remarks          : String?               = null,
    @SerializedName("user_id"            ) var userId           : Int?                  = null,
    @SerializedName("material_type_id"   ) var materialTypeId   : Int?                  = null,
    @SerializedName("truck_type_id"      ) var truckTypeId      : Int?                  = null,
    @SerializedName("status"             ) var status           : String?               = null,
    @SerializedName("trip_status"        ) var tripStatus       : Int?                  = null,
    @SerializedName("driver_rate"        ) var driverRate       : String?               = null,
    @SerializedName("driver_rate_id"     ) var driverRateId     : String?               = null,
    @SerializedName("customer_rate"      ) var customerRate     : String?               = null,
    @SerializedName("tracking_link"      ) var trackingLink     : String?               = null,
    @SerializedName("confirmed_date"     ) var confirmedDate    : String?               = null,
    @SerializedName("is_follow_up"       ) var isFollowUp       : Int?                  = null,
    @SerializedName("followup_date"      ) var followupDate     : String?               = null,
    @SerializedName("new_material_type"  ) var newMaterialType  : String?               = null,
    @SerializedName("new_body_type"      ) var newBodyType      : String?               = null,
    @SerializedName("new_truck_type"     ) var newTruckType     : String?               = null,
    @SerializedName("new_source_type"    ) var newSourceType    : String?               = null,
    @SerializedName("created_at"         ) var createdAt        : String               = "",
    @SerializedName("updated_at"         ) var updatedAt        : String               = "",
    @SerializedName("deleted_at"         ) var deletedAt        : String?               = null,
    @SerializedName("indent_rate"        ) var indentRate       : ArrayList<IndentRate> = arrayListOf(),

    @SerializedName("cancel_reasons" ) var cancelReasons : ArrayList<CancelReasons> = arrayListOf(),

    @SerializedName("truck_type_name") var truckTypeName : String? = null,
    @SerializedName("material_type_name") var materialTypeName : String? = null,

    @SerializedName("driver_details") var driverDetails : ArrayList<DriverDetail> = arrayListOf(),

    @SerializedName("courier_receipt_no" ) var courierReceiptNo : String? = null,
    @SerializedName("pod_soft_copy"      ) var podSoftCopy      : String? = null,
    @SerializedName("pod_courier"        ) var podCourier       : String? = null,
    @SerializedName("indent_id"          ) var indentId         : Int?    = null,

    @SerializedName("user"              ) var user            : User?   = User(),

    @SerializedName("suppliers") var suppliers : ArrayList<Suppliers>?=null,

    @SerializedName("supplier_name"      ) var supplierName     : String?               = null,
    @SerializedName("supplier_type"      ) var supplierType     : String?               = null,
    @SerializedName("contact_number"     ) var contactNumber    : String?               = null,
    @SerializedName("pan_card_number"    ) var panCardNumber    : String?               = null,
    @SerializedName("pan_card"           ) var panCard          : String?               = null,
    @SerializedName("business_card"      ) var businessCard     : String?               = null,
    @SerializedName("bank_details"       ) var bankDetails      : String?               = null,
    @SerializedName("memo"               ) var memo             : String?               = null,
    @SerializedName("eway_bill"          ) var ewayBill         : String?               = null,
    @SerializedName("trips_invoices"     ) var tripsInvoices    : String?               = null,
    @SerializedName("other_document"     ) var otherDocument    : String?               = null,
    @SerializedName("bank_name"          ) var bankName         : String?               = null,
    @SerializedName("ifsc_code"          ) var ifscCode         : String?               = null,
    @SerializedName("account_number"     ) var accountNumber    : String?               = null,
    @SerializedName("re_account_number"  ) var reAccountNumber  : String?               = null,

    @SerializedName("extra_costs" ) var extraCosts : ArrayList<ExtraCosts> = arrayListOf(),

    @SerializedName("leastAmt"          ) var leastAmt        : Double? = null,
    @SerializedName("followup_reason"  ) var followUpReason  : String?  = null,

    @SerializedName("created_by"       ) var createdBy      : String? = null,
    @SerializedName("pods" ) var pods : ArrayList<Pods> = arrayListOf()

    //@SerializedName("indent") var tripIndent: Indents? = Indents()

): Parcelable
data class WeightUnits (
    @SerializedName("kg"   ) var kg   : String? = null,
    @SerializedName("tons" ) var tons : String? = null)
@Parcelize
data class Pivot (
    @SerializedName("indent_id"        ) var indentId       : Int?    = null,
    @SerializedName("cancel_reason_id" ) var cancelReasonId : Int?    = null,
    @SerializedName("created_at"       ) var createdAt      : String? = null,
    @SerializedName("updated_at"       ) var updatedAt      : String? = null): Parcelable

@Parcelize
data class CancelReasons (
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("reason"     ) var reason    : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null,
    @SerializedName("pivot"      ) var pivot     : Pivot?  = Pivot()): Parcelable

@Parcelize
data class DriverDetail (

    @SerializedName("id"                   ) var id                 : Int?    = null,
    @SerializedName("indent_id"            ) var indentId           : Int?    = null,
    @SerializedName("driver_name"          ) var driverName         : String? = null,
    @SerializedName("driver_number"        ) var driverNumber       : String? = null,
    @SerializedName("vehicle_number"       ) var vehicleNumber      : String? = null,
    @SerializedName("vehicle_type"         ) var vehicleType        : String? = null,
    @SerializedName("truck_type"           ) var truckType          : String? = null,
    @SerializedName("new_truck_type"       ) var newTruckType       : String? = null,
    @SerializedName("driver_base_location" ) var driverBaseLocation : String? = null,
    @SerializedName("vehicle_photo"        ) var vehiclePhoto       : String? = null,
    @SerializedName("driver_license"       ) var driverLicense      : String? = null,
    @SerializedName("rc_book"              ) var rcBook             : String? = null,
    @SerializedName("insurance"            ) var insurance          : String? = null,
    @SerializedName("created_at"           ) var createdAt          : String? = null,
    @SerializedName("updated_at"           ) var updatedAt          : String? = null

): Parcelable

@Parcelize
data class ExtraCosts (

    @SerializedName("id"              ) var id             : Int?    = null,
    @SerializedName("indent_id"       ) var indentId       : Int?    = null,
    @SerializedName("extra_cost_type" ) var extraCostType  : String? = null,
    @SerializedName("amount"          ) var amount         : String? = null,
    @SerializedName("bill_copy"       ) var billCopy       : String? = null,
    @SerializedName("unloading_photo" ) var unloadingPhoto : String? = null,
    @SerializedName("bill_copies"     ) var billCopies     : String? = null,
    @SerializedName("is_confirmed"    ) var isConfirmed    : Int?    = null,
    @SerializedName("created_at"      ) var createdAt      : String? = null,
    @SerializedName("updated_at"      ) var updatedAt      : String? = null

): Parcelable

@Parcelize
data class Pods (

    @SerializedName("id"                 ) var id               : Int?    = null,
    @SerializedName("courier_receipt_no" ) var courierReceiptNo : String? = null,
    @SerializedName("pod_soft_copy"      ) var podSoftCopy      : String? = null,
    @SerializedName("pod_courier"        ) var podCourier       : String? = null,
    @SerializedName("pod_receipt_photo"  ) var podReceiptPhoto  : String? = null,
    @SerializedName("indent_id"          ) var indentId         : Int?    = null,
    @SerializedName("created_at"         ) var createdAt        : String? = null,
    @SerializedName("updated_at"         ) var updatedAt        : String? = null,
    @SerializedName("deleted_at"         ) var deletedAt        : String? = null

): Parcelable