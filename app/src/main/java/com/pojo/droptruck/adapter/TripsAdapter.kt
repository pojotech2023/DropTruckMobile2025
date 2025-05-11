package com.pojo.droptruck.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.AdapterTripsBinding
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.AppUtils.setTextWithEllipsize

class TripsAdapter(val context : Context, val mStatus: String, var enquryList: ArrayList<Indents>,
                   var iFace: TripsInterface) : RecyclerView.Adapter<TripsAdapter.EnquiryViewHolder>() {

                       var materialType = ""
                       var truckType:String? = ""
                       var sBodyType:String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnquiryViewHolder {
        val mBinding = AdapterTripsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EnquiryViewHolder(mBinding)

    }

    override fun getItemCount(): Int {
        return enquryList.size
    }

    override fun onBindViewHolder(holder: EnquiryViewHolder, position: Int) {

        with(holder) {

            with(enquryList[position]){
            //with(indents!!){

                mBinding.txtPick.text = this.pickupLocationId
                mBinding.txtDrop.text = this.dropLocationId

                mBinding.tripsLayout.setOnClickListener {
                    iFace.callTripsView(this.id,position,AppConstant.VIEW_ENQUIRY)
                }

                /*mBinding.viewConfirmIndent.setOnClickListener {
                    iFace.callTripsView(this.id,position,AppConstant.CONFIRM_INDENT)
                }*/

                mBinding.txtDt.text = "DT"+this.id //AppUtils.convertDate(this.createdAt)
                mBinding.txtDate.text = AppUtils.uTCToLocal(this.createdAt)//AppUtils.convertDate(this.createdAt)

                try {

                    if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SUPPLIER)) {
                        mBinding.txtDt.setOnClickListener {
                            iFace.callTripsView(this.id,position,AppConstant.CREATE_DRIVER)
                        }
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

                try{

                    if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SALES)) {
                        salesEnquiry(mBinding,mStatus)
                        mBinding.amount.text = this.customerRate
                        try {
                            mBinding.amount.setText((this.customerRate?.toDouble()?.plus(addExtraCost(this))).toString())
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                        mBinding.txtCustName.text = setTextWithEllipsize(this.customerName)
                    }else if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SUPPLIER)) {
                        supplierEnquiry(mBinding,mStatus,this)
                        mBinding.amount.text = this.driverRate

                        try {
                            mBinding.amount.setText((this.driverRate?.toDouble()?.plus(addExtraCost(this))).toString())
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        //mBinding.txtCustName.visibility = View.GONE
                        mBinding.txtCustName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.small_material_ic, 0, 0, 0)
                    }else if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.CUSTOMER)) {
                        customerEnquiry(mBinding,mStatus,this,position)
                        mBinding.txtCustName.visibility = View.GONE
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

                mBinding.valWeight.text = this.weight + " " + this.weightUnit

                //mBinding.txtCustName.text = this.customerName

                try {
                    if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.CUSTOMER)) {
                        this.truckTypeName?.let {
                            if (it.equals("Others", true)) {
                                mBinding.valTruck.text = setTextWithEllipsize(this.newTruckType)
                            } else {
                                mBinding.valTruck.text = setTextWithEllipsize(it)
                            }
                        }
                    }else {
                        this.truckTypeName?.let {
                            if (it.equals("Others", true)) {
                                mBinding.valTruck.text = setTextWithEllipsize(this.newTruckType)
                            } else {
                                mBinding.valTruck.text = setTextWithEllipsize(it)
                            }
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                try {
                    if (this.bodyType.equals("Others",true)) {
                        mBinding.valBody.text = setTextWithEllipsize(this.newBodyType)
                    }else{
                        mBinding.valBody.text = setTextWithEllipsize(this.bodyType)
                    }
                }catch (e:Exception) {
                    e.printStackTrace()
                }

                try {
                    if (this.cancelReasons!=null && this.cancelReasons.size>0) {
                        mBinding.valCancelReason.text = this.cancelReasons[0].reason
                        mBinding.cancelLay.visibility = View.VISIBLE
                    }
                }catch (e:Exception) {
                    e.printStackTrace()
                }

                mBinding.cancelActionLay.setOnClickListener {
                    iFace.callTripsView(this.id,position,AppConstant.LOSS)
                }


                mBinding.createDriver.setOnClickListener{
                    iFace.callTripsView(this.id,position,AppConstant.CREATE_DRIVER)
                }

                mBinding.createSupplier.setOnClickListener{
                    iFace.callTripsView(this.id,position,AppConstant.CREATE_SUPPLIER)
                }

                mBinding.createExtraCost.setOnClickListener{
                    iFace.callTripsView(this.id,position,AppConstant.CREATE_EXTRA_COST)
                }

                mBinding.createPod.setOnClickListener{
                    iFace.callTripsView(this.id,position,AppConstant.CREATE_POD)
                }

                if (this.remarks!=null) {
                    mBinding.valRemarks.text = this.remarks
                    mBinding.remarkLay.visibility = View.VISIBLE
                }else {
                    mBinding.remarkLay.visibility = View.GONE
                }


                mBinding.imgShare.setOnClickListener {

                    try {


                        // Details to share
                        val pickUpLocation = this.pickupLocationId
                        val dropLocation = this.dropLocationId
                        val enquiry = this.id.toString()
                        val weight = mBinding.valWeight.text

                        try {
                            materialType = if (this.materialTypeName.equals("Others",true)) {
                                this.newMaterialType.toString()
                            }else {
                                this.materialTypeName.toString()
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        try {
                            truckType = if (this.truckTypeName.equals("Others", true)) {
                                this.newTruckType
                            } else {
                                this.truckTypeName
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        try {
                            sBodyType = if (this.bodyType.equals("Others",true)) {
                                this.newBodyType
                            }else{
                                this.bodyType
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        val remarksSection = this.remarks?.let {
                            "\n*Remarks:* $it"
                        } ?: ""

                        // Construct the message
                        val message = String.format(
                            """
         *Enquiry No.:* %s
         *Pick Up Location:* %s
         *Drop Location:* %s
         *Truck Type:* %s
         *Body Type:* %s
         *Material Type:* %s
         *Weight:* %s
         %s
         """.trimIndent(),
                            enquiry, pickUpLocation, dropLocation, truckType, sBodyType,materialType,
                            weight,remarksSection
                        )

                        val sendIntent = Intent()
                        sendIntent.setAction(Intent.ACTION_SEND)
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
                        sendIntent.setType("text/plain")

                        //sendIntent.setPackage("com.whatsapp")
                        context.startActivity(Intent.createChooser(sendIntent, "Share via"))

                        /*if (sendIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(sendIntent)
                        } else {
                            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                        }*/

                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }

                if (!(prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.CUSTOMER))) {

                    try{
                        this.createdBy?.let {
                            mBinding.createdBy.text = it
                            if (it.contains("App",true)) {
                                mBinding.txtDt.setBackgroundResource(R.drawable.dotted_rectangle_app)
                                mBinding.txtDt.setTextColor(context.resources.getColor(R.color.white))
                                mBinding.tripsLayout.setBackgroundResource(R.drawable.card_app)
                            }else if (it.contains("Web",true)) {
                                mBinding.txtDt.setBackgroundResource(R.drawable.dotted_rectangle_web)
                                mBinding.txtDt.setTextColor(context.resources.getColor(R.color.white))
                                mBinding.tripsLayout.setBackgroundResource(R.drawable.card_web)
                            }else {
                                mBinding.txtDt.setBackgroundResource(R.drawable.dotted_rectangle)
                                mBinding.txtDt.setTextColor(context.resources.getColor(R.color.black))
                                mBinding.tripsLayout.setBackgroundResource(R.drawable.card_inhouse)
                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }else{
                    mBinding.createdBy.visibility = View.GONE
                }

                if (mStatus == AppConstant.COMPLETE_TRIP ||
                    this.status.equals(AppConstant.TRIPS_COMPLETED)) {
                    mBinding.imgShare.visibility = View.GONE
                    mBinding.showPod.visibility = View.VISIBLE
                }else{
                    mBinding.imgShare.visibility = View.VISIBLE
                    mBinding.showPod.visibility = View.GONE
                }

                mBinding.showPod.setOnClickListener {
                    iFace.callTripsView(this.id,position,AppConstant.SHOW_POD)
                }

                checkSupplier(mBinding,mStatus)

                if (mStatus == AppConstant.HISTORY) {
                    mBinding.imgShare.visibility = View.GONE
                }

            }
        }

    }

    private fun checkSupplier(mBinding: AdapterTripsBinding, mStatus: String) {
        if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SUPPLIER) &&
            mStatus.equals(AppConstant.COMPLETE_TRIP)) {
            mBinding.showPod.visibility = View.GONE
            mBinding.imgShare.visibility = View.GONE
        }

    }

    fun addExtraCost(indents: Indents):Double {
        var extraAmount = 0.0
        try{

            if (indents.extraCosts!=null && indents.extraCosts.size>0) {
                indents.extraCosts[0].amount?.let {
                    extraAmount = it.toDouble()
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return extraAmount
    }

    private fun supplierEnquiry(mBinding: AdapterTripsBinding, status: String, indents: Indents) {
        if (indents.materialTypeName.equals("Others")) {
            mBinding.txtCustName.text = setTextWithEllipsize(indents.newMaterialType)
        }else {
            mBinding.txtCustName.text = setTextWithEllipsize(indents.materialTypeName)
        }
        when(status) {
            AppConstant.CANCELLED -> {
                mBinding.cancelLay.visibility = View.VISIBLE
            }
            AppConstant.WFDRIVER -> {
                mBinding.createDriver.visibility = View.VISIBLE
            }
            AppConstant.LOADING -> {
                mBinding.createSupplier.visibility = View.VISIBLE
            }
            AppConstant.UNLOADING -> {
                mBinding.createExtraCost.visibility = View.VISIBLE
            }
            AppConstant.POD -> {
                mBinding.createPod.visibility = View.VISIBLE
            }
        }
    }

    private fun salesEnquiry(mBinding: AdapterTripsBinding, status: String) {

        when(status) {

            AppConstant.WFDRIVER -> {
                mBinding.cancelActionLay.visibility = View.VISIBLE
            }
            AppConstant.CANCELLED -> {
                mBinding.cancelLay.visibility = View.VISIBLE
            }

        }

    }

    private fun customerEnquiry(mBinding: AdapterTripsBinding, mStatus: String,
                                indents: Indents, position: Int) {

        mBinding.amount.text = indents.customerRate

        try {
            mBinding.amount.setText((indents.customerRate?.toDouble()?.plus(addExtraCost(indents))).toString())
        }catch (e:Exception){
            e.printStackTrace()
        }

        setStatus(mBinding.txtStatus,indents)
        //mBinding.txtStatus.text = indents.status
        mBinding.txtStatus.visibility = View.VISIBLE
        mBinding.txtCustName.text = setTextWithEllipsize(indents.customerName)

        //mBinding.imgShare.visibility = View.GONE

        if (indents.cancelReasons!=null && indents.cancelReasons.size>0) {

            if (indents.status.equals(AppConstant.TRIPS_COMPLETED)) {
                mBinding.showPod.visibility = View.VISIBLE
            }else {
                mBinding.showPod.visibility = View.GONE
            }
        }

        when(mStatus) {
            AppConstant.HISTORY ->{
                mBinding.imgShare.visibility = View.GONE
            }
        }
    }

    private fun setStatus(txtStatus: TextView, indent: Indents) {
        try {
            when(indent.status) {
                "0" -> {
                    //txtStatus.text = "UnQuoted"
                    try {
                        if (AppUtils.checkRate(indent.customerRate)) {
                            txtStatus.text = "Quoted"
                        } else {
                            txtStatus.text = "UnQuoted"
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                        txtStatus.text = "UnQuoted"
                    }

                }
                AppConstant.TRIPS_CONFIRMED -> {
                    txtStatus.text = "Waiting for driver"
                }
                AppConstant.TRIPS_LOADING -> {
                    txtStatus.text = "Loading"
                }
                AppConstant.TRIPS_UNLOADING -> {
                txtStatus.text = "Unloading"
                }
                AppConstant.TRIPS_ONTHEROAD -> {
                    txtStatus.text = "On the road"
                }
                AppConstant.TRIPS_POD -> {
                    txtStatus.text = "POD"
                }
                AppConstant.TRIPS_COMPLETED -> {
                    txtStatus.text = "Completed"
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {
            if (indent.cancelReasons!=null && indent.cancelReasons.size>0) {

                if (indent.status.equals(AppConstant.TRIPS_COMPLETED)) {
                    txtStatus.text = "Completed"
                }else {
                    txtStatus.text = "Cancelled"

                }
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }

    }

    class EnquiryViewHolder(var mBinding: AdapterTripsBinding) : RecyclerView.ViewHolder(mBinding.root)

    interface TripsInterface {
        fun callTripsView(id:Int,pos:Int,action:String)
    }

}