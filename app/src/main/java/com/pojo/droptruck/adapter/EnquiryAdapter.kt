package com.pojo.droptruck.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.AdapterEnquiryBinding
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.AppUtils.setTextWithEllipsize


class EnquiryAdapter(val context : Context, val mStatus: String, var enquryList: ArrayList<Indents>,
                     var iFace: EnquiryInterface) : RecyclerView.Adapter<EnquiryAdapter.EnquiryViewHolder>() {

    var materialType = ""
    var sTruckType:String? = ""
    var sBodyType:String? = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnquiryViewHolder {
        val mBinding = AdapterEnquiryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EnquiryViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return enquryList.size
    }

    override fun onBindViewHolder(holder: EnquiryViewHolder, position: Int) {

        with(holder) {
            with(enquryList[position]){
                mBinding.txtPick.text = this.pickupLocationId
                mBinding.txtDrop.text = this.dropLocationId

                mBinding.enquiryLayout.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.VIEW_ENQUIRY)
                }

                mBinding.viewConfirmIndent.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.CONFIRM_INDENT)
                }

                mBinding.editIndentUqo.setOnClickListener{
                    iFace.callEnquiryView(this.id,position,AppConstant.EDIT_ENQUIRY)
                }

                mBinding.amtQuoteLay.setOnClickListener{
                    iFace.callEnquiryView(this.id,position,AppConstant.RATE_ENQUIRY)
                }

                mBinding.dleIndentUqo.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.DELETE_ENQUIRY)
                }

                mBinding.confirmedLay.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.LOSS)
                }
                mBinding.dleIndent.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.LOSS)
                }

                mBinding.txtDt.text = "DT"+this.id //AppUtils.convertDate(this.createdAt)
                mBinding.txtDate.text = AppUtils.uTCToLocal(this.createdAt) //AppUtils.convertDate(this.createdAt)

                try{

                    if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SALES)) {
                        mBinding.amount.text = this.customerRate
                        //mBinding.amount.text = AppUtils.getLeastAmount(this.indentRate)
                        salesEnquiry(mBinding,mStatus,this,position)
                    }else if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.SUPPLIER)) {
                        mBinding.imgClone.visibility = View.GONE
                        mBinding.amount.text = AppUtils.getLeastAmountForSupplier(this.indentRate,prefs.getValueString(AppConstant.USER_ID))
                        supplierEnquiry(mBinding,mStatus,this)
                        mBinding.txtCustName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.small_material_ic, 0, 0, 0)
                    } else if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.CUSTOMER)) {
                        mBinding.imgClone.visibility = View.GONE
                        customerEnquiry(mBinding,mStatus,this,position)
                        //mBinding.txtCustName.visibility = View.GONE
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

                try {
                    if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.CUSTOMER)) {
                        //sTruckType = this.truckTypeName
                        mBinding.valTruck.text = setTextWithEllipsize(this.truckTypeName)
                        if (this.user!=null) {
                            mBinding.txtCustName.text = setTextWithEllipsize(this.user?.name)
                        }else {
                            mBinding.txtCustName.visibility = View.INVISIBLE
                        }

                        mBinding.customerCallLay.setOnClickListener {

                            try {

                                if (this.user!=null) {
                                    if(this.user?.contact!=null) {
                                        setCall(context,this.user?.contact.toString())
                                    }else {
                                        setCall(context,"7305063050")
                                    }
                                }else {
                                    setCall(context,"7305063050")
                                }
                            }catch (e:Exception){
                                setCall(context,"7305063050")
                                e.printStackTrace()
                            }

                        }

                    }else{
                        this.truckTypeName?.let {
                            if (it.equals("Others",true)) {
                                //sTruckType = this.newTruckType
                                mBinding.valTruck.text = setTextWithEllipsize(this.newTruckType)
                            }else{
                                //sTruckType = it
                                mBinding.valTruck.text = setTextWithEllipsize(it)
                            }
                        }
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

                mBinding.cancelActionLay.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.RESTORE_INDENT)
                }

                //sBodyType = this.bodyType
                mBinding.valBody.text = setTextWithEllipsize(this.bodyType)

                try {
                    if (this.bodyType.equals("Others",true)) {
                        //sBodyType = this.newBodyType
                        mBinding.valBody.text = setTextWithEllipsize(this.newBodyType)
                    }else{
                        //sBodyType = this.bodyType
                        mBinding.valBody.text = setTextWithEllipsize(this.bodyType)
                    }
                }catch (e:Exception) {
                    e.printStackTrace()
                }

                mBinding.valWeight.text = this.weight + " " + this.weightUnit

                try {
                    if (this.cancelReasons!=null && this.cancelReasons.size>0) {
                        mBinding.valCancelReason.text = this.cancelReasons[0].reason
                    }
                }catch (e:Exception) {
                    e.printStackTrace()
                }

                if (this.remarks!=null) {
                    mBinding.valRemarks.text = this.remarks
                    mBinding.remarkLay.visibility = View.VISIBLE
                }else {
                    mBinding.remarkLay.visibility = View.GONE
                }

                try {
                    if (mStatus.equals(AppConstant.FOLLOWUP)) {
                        this.followUpReason?.let {
                            mBinding.valRemarks.text = it
                            mBinding.remarkLay.visibility = View.VISIBLE
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                mBinding.imgShare.setOnClickListener {

                    try {
                        // Details to share
                        val pickUpLocation = this.pickupLocationId
                        val dropLocation = this.dropLocationId
                        //val truckType = mBinding.valTruck.text
                        //val bodyType = mBinding.valBody.text
                        val enquiry = this.id.toString()
                        val weight = mBinding.valWeight.text

                        try {
                            materialType = if (this.materialTypeName.equals("Others")) {
                                this.newMaterialType.toString()
                            }else {
                                this.materialTypeName.toString()
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        try {

                            if (prefs.getValueString(AppConstant.ROLE_ID).equals(AppConstant.CUSTOMER)) {
                                sTruckType = this.truckTypeName
                            }else {
                                sTruckType = if (this.truckTypeName.equals("Others", true)) {
                                    this.newTruckType
                                } else {
                                    this.truckTypeName
                                }
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
                         enquiry, pickUpLocation, dropLocation, sTruckType, sBodyType,materialType,
                            weight,remarksSection)

                        val sendIntent = Intent()
                        sendIntent.setAction(Intent.ACTION_SEND)
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
                        sendIntent.setType("text/plain")

                        //sendIntent.setPackage("com.whatsapp")
                        context.startActivity(Intent.createChooser(sendIntent, "Share via"));
                        /*if (sendIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(sendIntent)
                        } else {
                            context.startActivity(Intent.createChooser(sendIntent, "Share via"));
                        }*/

                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }

                try{
                    this.createdBy?.let {
                        mBinding.createdBy.text = it
                        if (it.contains("App",true)) {
                            mBinding.txtDt.setBackgroundResource(R.drawable.dotted_rectangle_app)
                            mBinding.txtDt.setTextColor(context.resources.getColor(R.color.white))
                            mBinding.enquiryLayout.setBackgroundResource(R.drawable.card_app)
                        }else if (it.contains("Web",true)) {
                            mBinding.txtDt.setBackgroundResource(R.drawable.dotted_rectangle_web)
                            mBinding.txtDt.setTextColor(context.resources.getColor(R.color.white))
                            mBinding.enquiryLayout.setBackgroundResource(R.drawable.card_web)
                        }else {
                            mBinding.txtDt.setBackgroundResource(R.drawable.dotted_rectangle)
                            mBinding.txtDt.setTextColor(context.resources.getColor(R.color.black))
                            mBinding.enquiryLayout.setBackgroundResource(R.drawable.card_inhouse)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                mBinding.imgClone.setOnClickListener {
                    iFace.callEnquiryView(this.id,position,AppConstant.CLONE_INDENT)
                }

            }
        }

    }

    private fun setCall(context: Context,number:String) {
        try {
            val uri = Uri.parse("tel:" +number)
            val intent = Intent(Intent.ACTION_DIAL,uri)
            context.startActivity(intent)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun customerEnquiry(mBinding: AdapterEnquiryBinding, mStatus: String,
                                indents: Indents, position: Int) {
        when(mStatus) {
            AppConstant.QUOTED -> {
                mBinding.amount.text = indents.customerRate
                mBinding.salesQuotedLayout.visibility = View.VISIBLE
                mBinding.customerCallLay.visibility = View.VISIBLE
            }
            AppConstant.UNQUOTED -> {
                mBinding.salesQuotedLayout.visibility = View.VISIBLE
                mBinding.viewConfirmIndent.visibility = View.GONE
                mBinding.customerCallLay.visibility = View.VISIBLE
            }
            AppConstant.HISTORY ->{
                mBinding.cancelLay.visibility = View.VISIBLE
            }
        }
    }

    private fun supplierEnquiry(mBinding: AdapterEnquiryBinding, status: String, indents: Indents) {

        if (indents.materialTypeName.equals("Others")) {
            mBinding.txtCustName.text = setTextWithEllipsize(indents.newMaterialType)
        }else {
            mBinding.txtCustName.text = setTextWithEllipsize(indents.materialTypeName)
        }

        when(status) {
            AppConstant.UNQUOTED -> {
                mBinding.amount.visibility = View.GONE
                mBinding.amtQuoteLay.visibility = View.VISIBLE
            }
            AppConstant.QUOTED -> {
                mBinding.amtQuoteLay.visibility = View.VISIBLE
            }
            AppConstant.CANCELLED -> {
                mBinding.cancelLay.visibility = View.VISIBLE
            }
        }
    }

    private fun salesEnquiry(
        mBinding: AdapterEnquiryBinding, status: String,
        indents: Indents, position: Int) {

        mBinding.txtCustName.text = setTextWithEllipsize(indents.customerName)
        mBinding.imgClone.visibility = View.GONE

        when(status) {

            AppConstant.UNQUOTED -> {
                //mBinding.imgClone.visibility = View.VISIBLE //for notification release only...
                mBinding.amount.visibility = View.GONE
                mBinding.salesUnquotedLayout.visibility = View.VISIBLE
            }
            AppConstant.QUOTED -> {
                mBinding.salesQuotedLayout.visibility = View.VISIBLE
            }
            AppConstant.CANCELLED -> {
                mBinding.cancelLay.visibility = View.VISIBLE
                mBinding.cancelFollowLay.visibility = View.VISIBLE
            }
            AppConstant.CONFIRMED -> {
                mBinding.confirmedLay.visibility = View.VISIBLE
            }
            AppConstant.FOLLOWUP -> {
                mBinding.cancelFollowLay.visibility = View.VISIBLE
                mBinding.dleIndent.visibility = View.VISIBLE

                try {
                    indents.followupDate?.let {
                        mBinding.txtDate.text = AppUtils.normalDateFormat(it)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

        }

    }

    class EnquiryViewHolder(var mBinding: AdapterEnquiryBinding) : RecyclerView.ViewHolder(mBinding.root)

    interface EnquiryInterface {
        fun callEnquiryView(id:Int,pos:Int,action:String)
    }

}