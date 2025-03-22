package com.pojo.droptruck.datastore.base

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.pojo.droptruck.R

open class BaseActivity : AppCompatActivity() {

    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog() {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setMessage("Loading...")
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.show()

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
        mProgressDialog?.dismiss()
    }

}