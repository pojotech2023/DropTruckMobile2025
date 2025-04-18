package com.pojo.droptruck.utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Handler
import android.util.Base64
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.pojo.droptruck.pojo.IndentRate
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Matcher
import java.util.regex.Pattern


object AppUtils {


    const val PREFERENCE_NAME="NSKTRADERS"
    //UAT...
    /*const val BASE_URL= "https://indiatruck.in/api/api/";
    const val IMAGE_BASE_URL= "https://indiatruck.in/";*/

    //Production...
    /*const val BASE_URL= "https://droptruck.in/api/api/"
    const val IMAGE_BASE_URL= "https://droptruck.in/"*/

    //Development...
    const val BASE_URL= "http://hiretruck.in/api/api/";
    const val IMAGE_BASE_URL= "http://hiretruck.in/";

    const val SalesType = "3"
    const val SupplierType = "4"

    // 3 -> Sales...
    // 4 -> Supplier...

    private val st1 = "UVVsNllWTjVRbTAxWDNs"
    private val st2 = "a1ptVjFRV0pGTWxwW"
    private val st3 = "VZXdFNibFpXTkZCVWR"
    private val st4 = "FeEZSMkpXYlZGcgo="


    const val APIDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
    const val OutPutDateFormat = "dd-MM-yyyy hh:mm a"
    const val SHOW_TEXT_LENGTH = 12

    fun convertDate(dateTime:String):String? {

        try {

            val inputFormat = SimpleDateFormat(APIDateTimeFormat, Locale.getDefault())
            val outputFormat = SimpleDateFormat(OutPutDateFormat, Locale.getDefault())

            // Parse input timestamp
            val date = inputFormat.parse(dateTime)

            // Format the date into dd-MM-yyyy format
            val formattedDate = date?.let { outputFormat.format(it) }
            return formattedDate
        }catch (e:Exception){
            e.printStackTrace()
        }

        return dateTime
    }

    fun getLeastAmountForSupplier(indentRate: ArrayList<IndentRate>, userId: String?): String {

        try {

            if (indentRate!=null && indentRate.size>0) {

                var leastAmt = 0.0

                for (i in 0 until indentRate.size) {

                    if (userId.toString() == indentRate[i].userId.toString()) {

                        val cAmount = indentRate[i].rate!!.toDouble()

                        if (leastAmt == 0.0) {
                            leastAmt = cAmount
                        }else {
                            if (cAmount<leastAmt) {
                                leastAmt = cAmount
                            }
                        }
                    }
                }
                return leastAmt.toString()
            }else {
                return ""
            }
        }catch (e:Exception) {
            /*if (indentRate!=null && indentRate.size>0){
                return indentRate[indentRate.size-1].rate.toString()
            }else {
                return ""
            }*/
            return ""
        }

        /*
        try {

            if (indentRate!=null && indentRate.size>0) {
                val minObject: IndentRate = indentRate.minBy { it.rate!! }
                return minObject.rate.toString()
            }else {
                return ""
            }
        }catch (e:Exception) {
            if (indentRate!=null && indentRate.size>0) {
                return indentRate[indentRate.size-1].rate.toString()
            }else {
                return ""
            }
        } */

    }

    fun showSupplierQuotedAmounts(indentRate: ArrayList<IndentRate>, userId: String): ArrayList<IndentRate> {
        var indentRateList =  ArrayList<IndentRate>()
        try {
            for (i in 0 until indentRate.size) {

                if (userId == indentRate[i].userId.toString()) {
                    indentRateList.add(indentRate[i])
                }
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }

        return indentRateList
    }

    fun getLeastAmount(indentRate: ArrayList<IndentRate>): String {

        try {

            if (indentRate!=null && indentRate.size>0) {

                var leastAmt = 0.0

                for (i in 0 until indentRate.size) {

                    val cAmount = indentRate[i].rate!!.toDouble()

                    if (i == 0) {
                        leastAmt = cAmount
                    }else {
                        if (cAmount<leastAmt) {
                            leastAmt = cAmount
                        }
                    }
                }

                return leastAmt.toString()

            }else {
                return ""
            }
        }catch (e:Exception) {
            if (indentRate!=null && indentRate.size>0){
                return indentRate[indentRate.size-1].rate.toString()
            }else {
                return ""
            }
        }

    }

    fun checkStrEmpty(status: String): Boolean {
        if (status.isNotEmpty() && status != "null") {
            return true
        }else{
            return false
        }
    }

    fun isValidPanCardNo(panCardNo: String?): Boolean {

        val regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}"

        val p: Pattern = Pattern.compile(regex)

        if (panCardNo == null) {
            return false
        }

        val m: Matcher = p.matcher(panCardNo)

        return m.matches()
    }

    fun isValidIFSCCode(str: String?): Boolean {
        // Regex to check valid IFSC Code.
        val regex = "^[A-Z]{4}0[A-Z0-9]{6}$"

        val p = Pattern.compile(regex)

        if (str == null) {
            return false
        }

        val m = p.matcher(str)

        return m.matches()
    }

    fun downLoadImage(string: String,context:Context): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    fun checkRate(customerRate: String?): Boolean {
        if (customerRate.isNullOrBlank() || customerRate.isEmpty() ||
            customerRate == "0" || customerRate == "0.0" || customerRate == "0.00"){
            return false
        }else {
            return true
        }
    }

    fun getDistance(my_latlong: LatLng, frnd_latlong: LatLng): String {
        val l1: Location = Location("One")
        l1.setLatitude(my_latlong.latitude)
        l1.setLongitude(my_latlong.longitude)

        val l2: Location = Location("Two")
        l2.setLatitude(frnd_latlong.latitude)
        l2.setLongitude(frnd_latlong.longitude)

        var distance: Float = l1.distanceTo(l2)
        var dist = "$distance M"

        if (distance > 1000.0f) {
            distance = distance / 1000.0f
            val d = String.format("%.2f", (distance))
            dist = "$d KM"
        }
        return dist
    }

    fun getMKStr(): String {
        return String(
            Base64.decode(
                Base64.decode(
                    st1 +
                            st2 +
                            st3 +
                            st4,
                    Base64.DEFAULT
                ),
                Base64.DEFAULT
            )
        )
    }

    fun normalDateFormat(dateTime:String?):String? {

        try {

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            // Parse input timestamp
            val date = inputFormat.parse(dateTime!!)

            // Format the date into dd-MM-yyyy format
            val formattedDate = date?.let { outputFormat.format(it) }
            return formattedDate
        }catch (e:Exception){
            e.printStackTrace()
        }

        return dateTime
    }

    fun uTCToLocal(datesToConvert: String): String? {

        try {

            val sdf = SimpleDateFormat(APIDateTimeFormat)
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            var gmt: Date? = null

            val sdfOutPutToSend = SimpleDateFormat(OutPutDateFormat)
            sdfOutPutToSend.timeZone = TimeZone.getDefault()

            gmt = sdf.parse(datesToConvert)
            return sdfOutPutToSend.format(gmt)
        } catch (e: Exception) {
            e.printStackTrace()
            return convertDate(datesToConvert)
        }

    }

    fun setTextWithEllipsize(str: String?):String {
        if (!str.isNullOrEmpty()) {
            if (str.length>SHOW_TEXT_LENGTH) {
                return str.substring(0, SHOW_TEXT_LENGTH) + "...";
            }else {
                return str
            }
        }else{
            return ""
        }
    }


    fun showProgressDialog(activity:Context):ProgressDialog {
        //Handler().postDelayed(Runnable {
            val mProgressDialog = ProgressDialog(activity)
            mProgressDialog.setMessage("Loading...")
            mProgressDialog.setCanceledOnTouchOutside(false)
            mProgressDialog.show()
        //},500)

        //callDelay()

        return mProgressDialog
    }
    fun dismissProgressDialog(mProgressDialog: ProgressDialog?) {
        Handler().postDelayed(Runnable {
            mProgressDialog?.dismiss()
        },500)

    }


}