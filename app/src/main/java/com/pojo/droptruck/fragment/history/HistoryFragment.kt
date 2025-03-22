package com.pojo.droptruck.fragment.history

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.tabs.TabLayoutMediator
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.TripsAdapter
import com.pojo.droptruck.databinding.FragmentHistoryBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showImgDialog

class HistoryFragment : BaseFragment(),TripsAdapter.TripsInterface  {//EnquiryAdapter.EnquiryInterface

    private lateinit var mBinding: FragmentHistoryBinding

    val enquiryArray = arrayOf("UnQuoted","Quoted")
    var userId: String? = ""
    val mViewModel: HistoryViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog
    lateinit var img: ImageView

    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHistoryBinding.inflate(inflater, container, false)

        val viewPager = mBinding.viewPager
        val tabLayout = mBinding.tabLayout

        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 1

        val tabAdapter = HistoryViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = tabAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = enquiryArray[position]
            /*tab.icon = ContextCompat.getDrawable(requireActivity(),
                enquiryArray[position])*/
        }.attach()

        mViewModel.resultLiveData.observe(viewLifecycleOwner, Observer {

            if (it!=null){
                dismissProgressDialog()
                when(it.status){
                    Status.SUCCESS -> {

                        it.data?.currentPage?.let {
                            currentPage = it.toInt()
                        }
                        it.data?.totalPages?.let {
                            totalPage = it
                        }

                        if (it.data?.dataIndents!=null && it.data.dataIndents!!.size>0){
                            //enquiryList.clear()
                            enquiryList.addAll(it.data.dataIndents)

                            /*if(it.data.data?.canceledIndents!=null &&
                                it.data.data?.canceledIndents?.size!! > 0) {
                                enquiryList.addAll(it.data.data?.canceledIndents!!)
                            }

                            if(it.data.data?.completedTrips!=null &&
                                it.data.data?.completedTrips?.size!! > 0) {
                                enquiryList.addAll(it.data.data?.completedTrips!!)
                            }
*/
                            callAdapter()
                        }else {
                            clearData()
                        }
                    }
                    Status.ERROR -> {
                        clearData()
                        shortToast(it.message.toString())
                    }
                }
            }
        })

        mBinding.nestedSV.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
            { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    currentPage++
                    if (currentPage <= totalPage) {
                        //call API...
                        initFn()
                    }
                }
            })

        initFn()

        return mBinding.root
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter()
    }

    private fun callAdapter() {
        val adapter = TripsAdapter(requireActivity(),AppConstant.HISTORY,enquiryList,this)
        mBinding.recyclerview.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        //initFn()
    }

    private fun initFn() {
        showProgressDialog()
        userId = prefs.getValueString(AppConstant.USER_ID)
        val role = prefs.getValueString(AppConstant.ROLE_ID)

        mViewModel.getHistoryList(role,userId!!,currentPage)

    }


    override fun callTripsView(id: Int, pos: Int, action: String) {
        try {
            when(action){
                AppConstant.VIEW_ENQUIRY -> {
                    val data = enquiryList[pos]
                    //callViewTripsDetails(data)
                }
                AppConstant.SHOW_POD -> {
                    dialog = showImgDialog(R.layout.dialog_show_img)

                    img = dialog.findViewById(R.id.imgView)
                    val imgClose: ImageView = dialog.findViewById(R.id.img_close)

                    imgClose.setOnClickListener {
                        dialog.dismiss()
                        dialog.cancel()
                    }

                    try {

                        enquiryList[pos].pods.let {

                            if (it.size>0) {

                                if (it[0].podSoftCopy!=null) {
                                    showProgressDialog()
                                    showImage(AppUtils.IMAGE_BASE_URL+it[0].podSoftCopy)
                                }else if (it[0].podCourier!=null) {
                                    showProgressDialog()
                                    showImage(AppUtils.IMAGE_BASE_URL+it[0].podCourier)
                                }

                            }

                        }

                    }catch (e:Exception) {
                        e.printStackTrace()
                    }
                    dialog.show()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showImage(imgUrl: String) {
        try {
            Glide.with(this).load(imgUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?,
                                                 target: Target<Drawable>?,
                                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        dismissProgressDialog()
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target:
                    Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        dismissProgressDialog()
                        return false
                    }
                })
                .into(img)
        }catch (e:Exception) {
            dismissProgressDialog()
            e.printStackTrace()
        }
    }

}