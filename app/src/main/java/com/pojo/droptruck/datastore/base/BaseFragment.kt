package com.pojo.droptruck.datastore.base

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment: Fragment() {

    var mProgressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

     fun showProgressDialog() {
         Handler().postDelayed(Runnable {
             mProgressDialog = ProgressDialog(activity)
             mProgressDialog!!.setMessage("Loading...")
             mProgressDialog!!.setCanceledOnTouchOutside(false)
             mProgressDialog!!.show()
         },500)

         //callDelay()

     }

    private fun callDelay() {
        val handler =  Handler()
        val runnable = Runnable {
            dismissProgressDialog()
        }
        handler.postDelayed(runnable,1000)
    }


    fun dismissProgressDialog() {
        Handler().postDelayed(Runnable {
            mProgressDialog?.dismiss()
        },400)

     }

}