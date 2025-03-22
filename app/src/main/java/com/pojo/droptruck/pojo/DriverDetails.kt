package com.pojo.droptruck.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DriverDetails(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : DriverData?   = DriverData()
)


data class DriverData (
    @SerializedName("indent"                ) var indent                : ArrayList<Indents>    = arrayListOf(),
    @SerializedName("driver"                ) var driver                : DriverDetail?           = DriverDetail(),
    @SerializedName("driverAmount"          ) var driverAmount          : DriverAmount?     = DriverAmount(),
    @SerializedName("supplierName"          ) var supplierName          : String?           = null,
    @SerializedName("customerAmount"        ) var customerAmount        : CustomerAmount?   = CustomerAmount(),
    @SerializedName("customerAdvanceAmount" ) var customerAdvanceAmount : Int?              = null,
    @SerializedName("supplierAdvanceAmount" ) var supplierAdvanceAmount : Int?              = null,
    @SerializedName("extraCostAmount"       ) var extraCostAmount       : Int?              = null,
    @SerializedName("extraCostType"         ) var extraCostType         : String?           = null,
    @SerializedName("podDetails"            ) var podDetails            : String?           = null
    //@SerializedName("suppliers"             ) var suppliers             : Suppliers?           = Suppliers(),
    //@SerializedName("suppliersAdvanceAmt"   ) var suppliersAdvanceAmt   : SuppliersAdvanceAmt? = SuppliersAdvanceAmt()

)
data class Driver (
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
)
data class DriverAmount (
    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("indent_id"         ) var indentId        : Int?    = null,
    @SerializedName("name"              ) var name            : String? = null,
    @SerializedName("rate"              ) var rate            : String? = null,
    @SerializedName("is_confirmed_rate" ) var isConfirmedRate : Int?    = null,
    @SerializedName("remarks"           ) var remarks         : String? = null,
    @SerializedName("user_id"           ) var userId          : Int?    = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null
)

data class CustomerAmount (
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("indent_id"  ) var indentId  : Int?    = null,
    @SerializedName("rate"       ) var rate      : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null
)

data class PodDetails (
    @SerializedName("id"                 ) var id               : Int?    = null,
    @SerializedName("courier_receipt_no" ) var courierReceiptNo : String? = null,
    @SerializedName("pod_soft_copy"      ) var podSoftCopy      : String? = null,
    @SerializedName("pod_courier"        ) var podCourier       : String? = null,
    @SerializedName("indent_id"          ) var indentId         : Int?    = null,
    @SerializedName("created_at"         ) var createdAt        : String? = null,
    @SerializedName("updated_at"         ) var updatedAt        : String? = null
)
data class SuppliersAdvanceAmt (
    @SerializedName("id"             ) var id            : Int?    = null,
    @SerializedName("indent_id"      ) var indentId      : Int?    = null,
    @SerializedName("payment_type"   ) var paymentType   : String? = null,
    @SerializedName("advance_amount" ) var advanceAmount : String? = null,
    @SerializedName("balance_amount" ) var balanceAmount : String? = null,
    @SerializedName("created_at"     ) var createdAt     : String? = null,
    @SerializedName("updated_at"     ) var updatedAt     : String? = null
)
data class ExtraCostType (
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
)

@Parcelize
data class Suppliers (

    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("supplier_name"     ) var supplierName    : String? = null,
    @SerializedName("supplier_type"     ) var supplierType    : String? = null,
    @SerializedName("company_name"      ) var companyName     : String? = null,
    @SerializedName("contact_number"    ) var contactNumber   : String? = null,
    @SerializedName("pan_card_number"   ) var panCardNumber   : String? = null,
    @SerializedName("pan_card"          ) var panCard         : String? = null,
    @SerializedName("business_card"     ) var businessCard    : String? = null,
    @SerializedName("bank_details"      ) var bankDetails     : String? = null,
    @SerializedName("memo"              ) var memo            : String? = null,
    @SerializedName("remarks"           ) var remarks         : String? = null,
    @SerializedName("eway_bill"         ) var ewayBill        : String? = null,
    @SerializedName("trips_invoices"    ) var tripsInvoices   : String? = null,
    @SerializedName("other_document"    ) var otherDocument   : String? = null,
    @SerializedName("bank_name"         ) var bankName        : String? = null,
    @SerializedName("ifsc_code"         ) var ifscCode        : String? = null,
    @SerializedName("account_number"    ) var accountNumber   : String? = null,
    @SerializedName("re_account_number" ) var reAccountNumber : String? = null,
    @SerializedName("indent_id"         ) var indentId        : Int?    = null,
    @SerializedName("status"            ) var status          : Int?    = null,
    @SerializedName("created_by"        ) var createdBy       : String? = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null,
    @SerializedName("deleted_at"        ) var deletedAt       : String? = null

): Parcelable