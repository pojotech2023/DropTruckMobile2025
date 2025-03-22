package com.pojo.droptruck.activity

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.barteksc.pdfviewer.PDFView
import com.pojo.droptruck.databinding.ActivityPdfviewBinding
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@AndroidEntryPoint
class PDFViewActivity : AppCompatActivity() {

    var imgUrl = ""
    var title = ""
    var id = ""
    private lateinit var mBinding: ActivityPdfviewBinding

    var mProgressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPdfviewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.imgBack.setOnClickListener { finish() }

        try {

            if (intent!=null) {
                imgUrl = intent.getStringExtra(AppConstant.IMAGE_URL).toString()
                title = intent.getStringExtra(AppConstant.IMAGE_TITLE).toString()
                id = intent.getStringExtra("id").toString()
                mBinding.txtTitle.text = title
                showProgressDialog()
                Log.d("imgUrl",imgUrl)

                RetrievePDFFromURL(mBinding.idPDFView,mProgressDialog!!).execute(imgUrl)

            }

        }catch (e:Exception){
            e.printStackTrace()
        }

        mBinding.imgDownload.setOnClickListener {
            downloadPdf(imgUrl)
        }

    }

    class RetrievePDFFromURL(pdfView: PDFView, mProgressDialog: ProgressDialog) :
        AsyncTask<String, Void, InputStream>() {

        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView
        val dialog: ProgressDialog = mProgressDialog

        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                // on below line we are creating an url
                // for our url which we are passing as a string.
                val url = URL(params.get(0))

                // on below line we are creating our http url connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                // on below line we are checking if the response
                // is successful with the help of response code
                // 200 response code means response is successful
                if (urlConnection.responseCode == 200) {
                    // on below line we are initializing our input stream
                    // if the response is successful.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            // on below line we are adding catch block to handle exception
            catch (e: Exception) {
                // on below line we are simply printing
                // our exception and returning null
                e.printStackTrace()
                dialog.dismiss()
                dialog.cancel()
                return null;
            }
            // on below line we are returning input stream.
            return inputStream;
        }

        // on below line we are calling on post execute
        // method to load the url in our pdf view.
        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            mypdfView.fromStream(result).load()
            dialog.dismiss()
            dialog.cancel()
        }

    }

    private fun downloadPdf(pdfUrl: String) {
        val downloader = AndroidDownloader(this@PDFViewActivity)
        downloader.downloadFile(pdfUrl,title,id)
        shortToast("Downloading...")
        //Toast.makeText(requireContext(), "Download clicked for $pdfUrl", Toast.LENGTH_SHORT).show()
    }

    class AndroidDownloader(private val context: Context) : Downloader {
        private val downloadManager = context.getSystemService(DownloadManager::class.java)

        override fun downloadFile(url: String,title: String,id: String): Long {
            val request = DownloadManager.Request(Uri.parse(url))
                .setMimeType("application/pdf")
                .setTitle("Downloading PDF")
                .setDescription("Downloading...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(
                    context,
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + "DT"+id + "_" + title+ ".pdf"
                )
            return downloadManager.enqueue(request)
        }
    }

    interface Downloader {
        fun downloadFile(url : String,title: String,id: String) : Long
    }

    fun showProgressDialog() {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setMessage("Loading...")
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.show()

    }

}