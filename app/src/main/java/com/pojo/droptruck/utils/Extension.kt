package com.pojo.droptruck.utils

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pojo.droptruck.activity.PDFViewActivity
import com.pojo.droptruck.activity.viewenquiry.EnquiryViewActivity
import com.pojo.droptruck.activity.confirmindent.ConfirmIndentActivity
import com.pojo.droptruck.activity.createcost.CreateExtraCostActivity
import com.pojo.droptruck.activity.createsupplier.CreateSupplierActivity
import com.pojo.droptruck.activity.driver.CreateDriverActivity
import com.pojo.droptruck.activity.imgview.ImageViewActivity
import com.pojo.droptruck.activity.indents.IndentActivity
import com.pojo.droptruck.activity.newmain.NewMainActivity
import com.pojo.droptruck.activity.pod.CreatePODActivity
import com.pojo.droptruck.activity.viewenquiry.TripsViewActivity
import com.pojo.droptruck.activity.webview.WebViewActivity
import com.pojo.droptruck.pojo.Indents
import com.pojo.droptruck.signin.SigninActivity
import com.pojo.droptruck.signup.SignupActivity
import com.pojo.droptruck.user.act.CustomerIndentActivity
import com.pojo.droptruck.user.act.CustomerMainActivity
import com.pojo.droptruck.user.act.ViewIndentWithMapActivity


fun Activity.shortToast(msg:String) {
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}
fun Fragment.shortToast(msg:String) {
    Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show()
}

fun Activity.longToast(msg:String) {
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
}
fun Fragment.longToast(msg:String) {
    Toast.makeText(activity,msg, Toast.LENGTH_LONG).show()
}


fun Activity.LoginActivity() {
    val intent = Intent(this, SigninActivity::class.java)
    startActivity(intent)
    finish()
}

fun Activity.callSignUpActivity(mob:String) {
    val intent = Intent(this, SignupActivity::class.java)
    intent.putExtra("mob",mob)
    startActivity(intent)
    //finish()
}

fun Fragment.callCreateIndent() {
    val intent = Intent(activity, IndentActivity::class.java)
    startActivity(intent)
}

fun Activity.callNewMainActivity() {
    val intent = Intent(this, NewMainActivity::class.java)
    startActivity(intent)
    finish()
}


fun Activity.callCreateIndent() {
    val intent = Intent(this, IndentActivity::class.java)
    startActivity(intent)
}

fun Fragment.callViewEnquiry(data: Indents) {
    val intent = Intent(requireActivity(), EnquiryViewActivity::class.java)
    intent.putExtra("data",data)
    startActivity(intent)
}

fun Fragment.callViewEnquiry(data: Indents,a:String) {
    val intent = Intent(requireActivity(), EnquiryViewActivity::class.java)
    intent.putExtra("data",data)
    startActivity(intent)
}

fun Fragment.showCurrencyDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(requireActivity())
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(true)

    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
    val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
    mDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    //mDialog.window?.setBackgroundDrawableResource(R.drawable.white_dialog_bg)

    return mDialog
}

fun Fragment.callConfirmIndents(id:String,data: Indents) {
    val intent = Intent(requireActivity(), ConfirmIndentActivity::class.java)
    intent.putExtra("id",id)
    intent.putExtra("data",data)
    startActivity(intent)
}

fun Fragment.callIndentEdit(data: Indents) {
    val intent = Intent(activity, IndentActivity::class.java)
    intent.putExtra("data",data)
    startActivity(intent)
}

fun Fragment.LoginActivity() {
    val intent = Intent(activity, SigninActivity::class.java)
    startActivity(intent)
}

fun Activity.showCustRateDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(this)
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(true)

    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
    val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
    mDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    //mDialog.window?.setBackgroundDrawableResource(R.drawable.white_dialog_bg)

    return mDialog
}

fun Fragment.callCreateDriver(id:Int) {
    val intent = Intent(activity,CreateDriverActivity::class.java)
    intent.putExtra("id",id.toString())
    startActivity(intent)
}
fun Fragment.callCreateSupplier(id:Int) {
    val intent = Intent(activity,CreateSupplierActivity::class.java)
    intent.putExtra("id",id.toString())
    startActivity(intent)
}

fun Activity.showCurrencyDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(this)
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(true)

    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
    mDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    return mDialog
}
fun Activity.callCustomerCreateIndent() {
    val intent = Intent(this, CustomerIndentActivity::class.java)
    startActivity(intent)
}

//fun Fragment.callViewTripsDetails(data: Indents,role: String,userId: String) {
fun Fragment.callViewTripsDetails(data: Indents) {
    val intent = Intent(requireActivity(), TripsViewActivity::class.java)
    intent.putExtra("data",data)
    startActivity(intent)
}

fun Fragment.callViewTripsDetails(data: Indents,status: String) {
    val intent = Intent(requireActivity(), TripsViewActivity::class.java)
    intent.putExtra("data",data)
    intent.putExtra("status",status)
    startActivity(intent)
}

fun Activity.callImageViewActivity(url:String,title:String,id:String) {
    if (url.contains(".pdf")) {
        callPDFViewActivity(url,title,id)
    }else {
        val intent = Intent(this, ImageViewActivity::class.java)
        intent.putExtra(AppConstant.IMAGE_URL,url)
        intent.putExtra(AppConstant.IMAGE_TITLE,title)
        intent.putExtra("id",id)
        startActivity(intent)

    }

}

fun Fragment.callCreateExtraCost(id:Int) {
    val intent = Intent(activity,CreateExtraCostActivity::class.java)
    intent.putExtra("id",id.toString())
    startActivity(intent)
}

fun Fragment.callCreatePOD(id:Int) {
    val intent = Intent(activity,CreatePODActivity::class.java)
    intent.putExtra("id",id.toString())
    startActivity(intent)
}

fun Fragment.callWebView(link:String) {
    val intent = Intent(activity,WebViewActivity::class.java)
    intent.putExtra("id",id.toString())
    startActivity(intent)
}
fun Activity.callWebView(link:String) {
    val intent = Intent(this,WebViewActivity::class.java)
    intent.putExtra("link",link)
    startActivity(intent)
}

fun Activity.showAppUpdateDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(this)
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(false)

    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
    mDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    return mDialog
}
fun Activity.showOTPDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(this)
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(false)

    val width = (resources.displayMetrics.widthPixels * 0.98).toInt()
    mDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    return mDialog
}
fun Activity.callPDFViewActivity(url:String,title:String,id:String) {
    val intent = Intent(this, PDFViewActivity::class.java)
    intent.putExtra(AppConstant.IMAGE_URL,url)
    intent.putExtra(AppConstant.IMAGE_TITLE,title)
    intent.putExtra("id",id)
    startActivity(intent)

}

fun Activity.callCustomerActivity() {
    val intent = Intent(this, CustomerMainActivity::class.java)
    startActivity(intent)
    finish()
}

fun Fragment.callCustomerViewTripsDetails(data: Indents?, status: String,
    from: String, indentId: String) {

    val intent = Intent(requireActivity(), ViewIndentWithMapActivity::class.java)
    intent.putExtra("data",data)
    intent.putExtra("status",status)
    intent.putExtra("from",from)
    intent.putExtra(AppConstant.INDENT,indentId)
    startActivity(intent)
}

fun Fragment.showCustRateDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(requireActivity())
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(true)
    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
    mDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    return mDialog
}

fun Fragment.showImgDialog(dialogLayout: Int): Dialog {

    val mDialog = Dialog(requireActivity())
    mDialog.setContentView(dialogLayout)
    mDialog.setCanceledOnTouchOutside(true)
    val width = (resources.displayMetrics.widthPixels * 1).toInt()
    val height = (resources.displayMetrics.heightPixels * 1).toInt()
    //mDialog.window?.setLayout(width, height)
    mDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    return mDialog
}