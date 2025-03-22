package com.pojo.droptruck.activity.viewenquiry

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pojo.droptruck.adapter.AlgorithmAdapter
import com.pojo.droptruck.databinding.ActivityEnquiryViewBinding
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnquiryViewActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityEnquiryViewBinding
    val mViewModel: ViewEnquiryViewModel by viewModels()

    var adapter: AlgorithmAdapter? = null
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEnquiryViewBinding.inflate(layoutInflater)
        mBinding.obj = mViewModel
        mBinding.lifecycleOwner = this
        setContentView(mBinding.root)

        val role = prefs.getValueString(AppConstant.ROLE_ID)
        userId = prefs.getValueString(AppConstant.USER_ID)!!

        prefs.save(AppConstant.VIEW_INDENT,"VIEW_INDENT")

        if (role.equals(AppConstant.SUPPLIER)) {
            mBinding.notSupplier.visibility = View.GONE
        }else if (role.equals(AppConstant.SALES)) {
            mBinding.titleCustomerRate.visibility = View.VISIBLE
            mBinding.txtRate.visibility = View.VISIBLE
        }

        if (role.equals(AppConstant.CUSTOMER)) {
            mBinding.titleDriverRate.visibility = View.GONE
            mBinding.driverRateSpinner.visibility = View.GONE

            mBinding.titleCustomerRate.visibility = View.VISIBLE
            mBinding.txtRate.visibility = View.VISIBLE

        }

        mBinding.imgLay.setOnClickListener { finish() }

        try {
            if (intent!=null){
                val data = intent.getParcelableExtra("data") as? Indents
                if (data!=null){
                    mViewModel.setIndents(data)
                    mBinding.txtEnq.text = "DT"+data?.id.toString()
                    try {

                        mBinding.txtRate.text = data.customerRate

                        if (data.indentRate!=null && data.indentRate.isNotEmpty()) {

                            if (role.equals(AppConstant.SUPPLIER)) {

                                val quotedAmountList = AppUtils.showSupplierQuotedAmounts(data.indentRate,userId)
                                adapter = AlgorithmAdapter(this, quotedAmountList,AppConstant.EnquiryViewActivity)

                            }
                            //else if (role.equals(AppConstant.SALES)) {
                            else {
                                adapter = AlgorithmAdapter(this, data.indentRate,AppConstant.EnquiryViewActivity)
                            }

                            mBinding.driverRateSpinner.adapter = adapter
                        }
                    }catch (e:Exception) {
                        e.printStackTrace()
                    }


                    try {

                        data.truckTypeName?.let {
                            if (it.equals("Others",true)) {
                                mBinding.valTruck.text = data.newTruckType
                            }else{
                                mBinding.valTruck.text = it
                            }
                        }

                        if (data.bodyType.equals("Others",true)) {
                            mBinding.valBody.text = data.newBodyType
                        }else{
                            mBinding.valBody.text = data.bodyType
                        }

                        if (data.materialTypeName.equals("Others",true)) {
                            mBinding.valMatType.text = data.newMaterialType
                        }else{
                            mBinding.valMatType.text = data.materialTypeName
                        }

                    }catch (e:Exception) {
                        e.printStackTrace()
                    }

                    try {

                        data.createdAt?.let {
                            mBinding.followupLay.visibility = View.VISIBLE
                            mBinding.followUpDate.text = AppUtils.normalDateFormat(data.createdAt)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }




    }
}