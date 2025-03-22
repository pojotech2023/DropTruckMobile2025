package com.pojo.droptruck.activity.imgview

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.pojo.droptruck.databinding.ActivityImageViewBinding
import com.pojo.droptruck.utils.AppConstant
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors
import javax.annotation.Nullable
import com.bumptech.glide.request.target.Target
import com.pojo.droptruck.datastore.base.BaseActivity
import com.pojo.droptruck.utils.AppUtils


@AndroidEntryPoint
class ImageViewActivity : BaseActivity() {

    private lateinit var mBinding: ActivityImageViewBinding
    var imgUrl = ""
    var title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.imgBack.setOnClickListener { finish() }

        try {

            if (intent!=null) {
                imgUrl = intent.getStringExtra(AppConstant.IMAGE_URL).toString()
                title = intent.getStringExtra(AppConstant.IMAGE_TITLE).toString()
                mBinding.txtTitle.text = title
                showProgressDialog()
                Glide.with(this).load(imgUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?,
                                                     target: Target<Drawable>?,
                                                     dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            dismissProgressDialog()
                            return false
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target:Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            dismissProgressDialog()
                            return false
                        }
                    })
                    .into(mBinding.imgView)

                Log.d("imgUrl",imgUrl)

            }

        }catch (e:Exception){
            e.printStackTrace()
        }

        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        mBinding.imgDownload.setOnClickListener {

            myExecutor.execute {
                var mImage = AppUtils.downLoadImage(imgUrl,this@ImageViewActivity)
                myHandler.post {
                    if(mImage!=null){
                        mSaveMediaToStorage(mImage)
                    }
                }
            }
        }

    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = title +  "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }

}