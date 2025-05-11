package com.pojo.droptruck.activity.newmain

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pojo.droptruck.BuildConfig
import com.pojo.droptruck.R
import com.pojo.droptruck.pojo.FollowUpData
import com.pojo.droptruck.prefs
import com.pojo.droptruck.signin.SignInViewModel
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.LoginActivity
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.callCustomerCreateIndent
import com.pojo.droptruck.utils.longToast
import com.pojo.droptruck.utils.showAppUpdateDialog
import com.pojo.droptruck.utils.showFollowUpDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class NewMainActivity : AppCompatActivity() {

    var bottomNavigationView: BottomNavigationView? = null
    var navController: NavController? = null
    //https://www.youtube.com/watch?v=hV8d3zfa5V0
    var navHost: NavHostFragment? = null
    var graph: NavGraph? = null

    var fab: FloatingActionButton?=null

    val mViewModel: SignInViewModel by viewModels()
    lateinit var dialog: Dialog
    lateinit var followUpDialog: Dialog
    var myTimer: Timer?=null
    var userId:String = ""
    lateinit var iActDialog: Dialog
    var role:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView!!.inflateMenu(R.menu.supplier_sales_menu)
        //bottomNavigationView!!.menu.getItem(2).isEnabled = false

        navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment?
        navController = navHost!!.navController
        val navInflater = navController!!.navInflater
        graph = navInflater.inflate(R.navigation.nav_graph)
        //graph = navInflater.inflate(R.navigation.main_nav_graph);
        fab = findViewById(R.id.fab)
        userId = prefs.getValueString(AppConstant.USER_ID).toString()
        role = prefs.getValueString(AppConstant.ROLE_ID).toString()

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (role.equals(AppConstant.CUSTOMER)) {
            bottomNavigationView!!.menu.getItem(1).isVisible = false
            bottomNavigationView!!.menu.getItem(0).icon = resources.getDrawable(R.drawable.enqiry_trip)
            bottomNavigationView!!.menu.getItem(0).title = "Enquirys"
            fab!!.visibility = View.VISIBLE
            graph!!.setStartDestination(R.id.dashboardFragment)
        }else if (role.equals(AppConstant.SUPPLIER)) {
            fab!!.visibility = View.GONE
            //bottomNavigationView!!.menu.getItem(0).isVisible = false
            bottomNavigationView!!.menu.getItem(3).isVisible = false
            //graph!!.setStartDestination(R.id.enquiryTripFragment)
            graph!!.setStartDestination(R.id.dashboardFragment)
        }else {
            fab!!.visibility = View.GONE
            bottomNavigationView!!.menu.getItem(3).isVisible = false
            graph!!.setStartDestination(R.id.dashboardFragment)

            if (prefs.getValueString(AppConstant.CURRENT_DATE).isNullOrEmpty() ||
                !prefs.getValueString(AppConstant.CURRENT_DATE).equals(currentDate)) {
                mViewModel.checkFollowUp(userId)
            }

        }

        //graph!!.setStartDestination(R.id.dashboardFragment)
        navController!!.graph = graph!!

        NavigationUI.setupWithNavController(bottomNavigationView!!, navController!!)

        fab!!.setOnClickListener {
            callCustomerCreateIndent()
        }

        mViewModel.callVersionCheck()

        mViewModel.versionLiveData.observe(this, Observer {
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {
                        if (it.data?.status == 200) {

                            try {
                                val playStoreVersionCode = it.data.data.vCode.toInt()

                                val versionCode = BuildConfig.VERSION_CODE

                                if (versionCode<playStoreVersionCode) {
                                    callUpdateDialog()
                                }
                            }catch (e:Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d("error",it.message.toString())
                    }
                }
            }
        })

        getFcmToken()

        mViewModel.checkStatusLiveData.observe(this,Observer {
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {

                        if (it.data?.status == 400) {
                            if (it.data.message.toString().contains ("Inactive")){
                                callInActiveDialog(it.data.message.toString())
                            }
                        }

                    }
                    Status.ERROR -> {}
                }
            }
        })

        askPermissions()

        mViewModel.followUpLiveData.observe(this,Observer {
            if (it!=null){
                when(it.status){
                    Status.SUCCESS -> {

                        if (it.data?.status == 200) {
                            callFollowUpDialog(it.data.data)
                            prefs.save(AppConstant.CURRENT_DATE,currentDate)
                        }

                    }
                    Status.ERROR -> {

                    }
                }
            }
        })


    }

    private fun callInActiveDialog(msg: String) {
        try {
            iActDialog = showAppUpdateDialog(R.layout.dialog_app_update)

            val btnUpdate: TextView = iActDialog.findViewById(R.id.btn_update)
            val btnCancel: TextView = iActDialog.findViewById(R.id.btn_cancel)
            val title: TextView = iActDialog.findViewById(R.id.title)
            val content: TextView = iActDialog.findViewById(R.id.content)
            btnCancel.visibility = View.GONE
            title.setText("Account InActivate")
            content.setText(msg)
            btnUpdate.setText("OK")

            cancelTimer()

            btnUpdate.setOnClickListener {
                prefs.clearAll()
                iActDialog.dismiss()
                finish()
                LoginActivity()
            }

            iActDialog.show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun callUpdateDialog() {

        try {
            dialog = showAppUpdateDialog(R.layout.dialog_app_update)

            val btnUpdate: TextView = dialog.findViewById(R.id.btn_update)
            val btnCancel: TextView = dialog.findViewById(R.id.btn_cancel)
            btnCancel.visibility = View.GONE

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnUpdate.setOnClickListener {
                openPlayStore()
                dialog.dismiss()
            }

            dialog.show()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    fun openPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getFcmToken() {

        try {

            //this creating token
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    // this fail
                    if (!task.isSuccessful) {
                        Log.d(
                            "TAG",
                            "Fetching FCM registration token failed",
                            task.exception
                        )
                        return@addOnCompleteListener
                    }
                    //this token
                    val token = task.result
                    Log.d("TAG", "Token: $token")
                    //to showing
                    //Toast.makeText(this@SigninActivity, "token $token", Toast.LENGTH_SHORT).show()
                    mViewModel.updateFCMToken(userId,role,token)
                }

        }catch (e:Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    private fun startTimer() {
        try {

            if (myTimer==null) {
                myTimer = Timer()
            }else {
                myTimer?.cancel()
                myTimer = Timer()
            }

            myTimer?.schedule(object : TimerTask() {
                override fun run() {
                    mViewModel.checkUserStatus(userId)
                }
            }, 0,60000)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }

    private fun cancelTimer() {
        try {
            myTimer?.cancel()
            myTimer = null
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun askPermissions() {

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES,
                //Manifest.permission.POST_NOTIFICATIONS
            )
            .withListener(
                object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                }
            )
            .check()

    }

    private fun callFollowUpDialog(data: FollowUpData?) {

        try {
            followUpDialog = showFollowUpDialog(R.layout.dialog_followup)

            val btnOk: TextView = followUpDialog.findViewById(R.id.btn_ok)
            val today: TextView = followUpDialog.findViewById(R.id.today_count)
            val tomorrow: TextView = followUpDialog.findViewById(R.id.tomorrow_count)

            btnOk.setOnClickListener {
                followUpDialog.dismiss()
            }

            today.text = data?.today.toString()
            tomorrow.text = data?.tomorrow.toString()

            followUpDialog.show()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}