package com.pojo.droptruck.trips

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pojo.droptruck.R
import com.pojo.droptruck.adapter.TripsAdapter
import com.pojo.droptruck.databinding.FragmentWaitingForDriverBinding
import com.pojo.droptruck.datastore.base.BaseFragment
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.prefs
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.AppUtils
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callViewTripsDetails
import com.pojo.droptruck.utils.shortToast
import com.pojo.droptruck.utils.showImgDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedTripsFragment : Fragment(),TripsAdapter.TripsInterface {

    lateinit var mainBinding: FragmentWaitingForDriverBinding
    val mViewModel: TripsViewModel by viewModels()
    var enquiryList = ArrayList<Indents>()
    lateinit var dialog: Dialog

    val TAG = "QuotedFragment"
    var userId: String? = ""
    lateinit var img: ImageView

    //becz of pagination...
    var currentPage:Int = 1
    var totalPage:Int = 1

    var mProgressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mainBinding = FragmentWaitingForDriverBinding.inflate(inflater, container, false)

        mViewModel.completedListLiveData.observe(viewLifecycleOwner, Observer {

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

                        if (it.data?.dataIndents != null &&  it.data.dataIndents.size>0){
                            if (currentPage == 1){
                                enquiryList.clear()
                            }

                            enquiryList.addAll(it.data.dataIndents)
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

        mainBinding.nestedSV.setOnScrollChangeListener(
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

        //initFn() //becz of pagination...

        return mainBinding.root
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        currentPage = 1
        clearData()
        initFn()
    }

    private fun initFn() {
        showProgressDialog()
        userId = prefs.getValueString(AppConstant.USER_ID)
        val role = prefs.getValueString(AppConstant.ROLE_ID)

        mViewModel.callCompleteTripsList(userId,role,currentPage)
    }

    private fun clearData() {
        enquiryList.clear()
        callAdapter()
    }

    private fun callAdapter() {
        val tripsAdapter = TripsAdapter(requireActivity(), AppConstant.COMPLETE_TRIP,enquiryList,this)
        mainBinding.tripsRecyclerview.adapter = tripsAdapter
    }

    override fun callTripsView(id: Int, pos: Int, action: String) {
        when(action){
            AppConstant.VIEW_ENQUIRY -> {
                val data = enquiryList[pos]
                callViewTripsDetails(data)
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

    fun showProgressDialog() {
        Handler().postDelayed(Runnable {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setMessage("Loading...")
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.show()
        },0)

        //callDelay()

    }

    fun dismissProgressDialog() {
        Handler().postDelayed(Runnable {
            mProgressDialog?.dismiss()
        },400)

    }

}