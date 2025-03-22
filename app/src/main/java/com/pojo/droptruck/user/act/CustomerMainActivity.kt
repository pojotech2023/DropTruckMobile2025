package com.pojo.droptruck.user.act

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pojo.droptruck.BuildConfig
import com.pojo.droptruck.R
import com.pojo.droptruck.databinding.ActivityMainBinding
import com.pojo.droptruck.prefs
import com.pojo.droptruck.signin.SignInViewModel
import com.pojo.droptruck.utils.AppConstant
import com.pojo.droptruck.utils.LoginActivity
import com.pojo.droptruck.utils.Status
import com.pojo.droptruck.utils.showAppUpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class CustomerMainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    var navController: NavController? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    //https://www.youtube.com/watch?v=hV8d3zfa5V0
    var navHost: NavHostFragment? = null
    var mBinding: ActivityMainBinding? = null
    var profileLayout: RelativeLayout? = null
    var historyLay: RelativeLayout? = null
    var orderLay: RelativeLayout? = null
    var indentLay: RelativeLayout? = null
    var logoutLay: RelativeLayout? = null

    var page_title: TextView? = null
    var mImgMenu: ImageView? = null

    var user_name: TextView? = null
    var user_phone: TextView? = null
    val mViewModel: SignInViewModel by viewModels()
    lateinit var dialog: Dialog

    var myTimer: Timer?=null
    var userId:String = ""
    lateinit var iActDialog: Dialog
    var role:String = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

        profileLayout = findViewById(R.id.profile_lay)
        historyLay = findViewById(R.id.history_lay)
        orderLay = findViewById(R.id.orders_lay)
        indentLay = findViewById(R.id.bd_lay)
        logoutLay = findViewById(R.id.logout_lay)
        page_title = findViewById(R.id.page_title)
        mImgMenu = findViewById(R.id.img_menu)
        user_name = findViewById(R.id.user_name)
        user_phone = findViewById(R.id.user_phone)

        try{
            user_name?.setText(prefs.getValueString(AppConstant.NAME))
            user_phone?.setText(prefs.getValueString(AppConstant.CONTACT))
            userId = prefs.getValueString(AppConstant.USER_ID).toString()
            role = prefs.getValueString(AppConstant.ROLE_ID).toString()
        }catch (e: Exception){
            e.printStackTrace()
        }


        //New
        navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment?
        navController = navHost!!.navController
        val navInflater = navController!!.navInflater
        graph = navInflater.inflate(R.navigation.nav_graph)

        try{
            if (prefs.getValueString(AppConstant.CREATED_ENQ_NUMBER).isNullOrEmpty()) {
                graph!!.setStartDestination(R.id.tripFragment)
            }else {
                graph!!.setStartDestination(R.id.customerCreateIndentFragment)
            }
        }catch (e:Exception){
            e.printStackTrace()
            graph!!.setStartDestination(R.id.customerCreateIndentFragment)
        }

        //graph!!.setStartDestination(R.id.customerCreateIndentFragment)
        navController!!.graph = graph!!
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationViewDrawer)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close)
        navigationView!!.setNavigationItemSelectedListener(this)

        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        NavigationUI.setupWithNavController(navigationView!!, navController!!)

        profileLayout!!.setOnClickListener { openPage(R.id.profileFragment,"Profile") }
        historyLay!!.setOnClickListener { openPage(R.id.historyFragment,"History") }
        orderLay!!.setOnClickListener { openPage(R.id.tripFragment,"Orders") }
        logoutLay!!.setOnClickListener {
            drawerLayout!!.closeDrawers()
            callLogout()
        }
        indentLay!!.setOnClickListener {
            clearIndentPref()
            openPage(R.id.customerCreateIndentFragment,"Create Indent")
        }

        mImgMenu?.setOnClickListener {
            drawerLayout?.openDrawer(Gravity.START)
        }

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

        getFcmToken()
        askPermissions()

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

    private fun callLogout() {
        prefs.clearAll()
        finish()
        LoginActivity()
    }

    fun openPage(page: Int,title: String) {
        drawerLayout!!.closeDrawers()
        navController!!.navigate(page)
        page_title?.text = title
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout!!.closeDrawers()
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        @JvmField
        var drawerLayout: DrawerLayout? = null
        var navigationView: NavigationView? = null
        var graph: NavGraph? = null
    }


    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun clearIndentPref() {
        prefs.deleteKey(AppConstant.FROM_LOCATION)
        prefs.deleteKey(AppConstant.TO_LOCATION)
        prefs.deleteKey(AppConstant.AMOUNT)
        prefs.deleteKey(AppConstant.IS_NEW_INDENT_CREATED)
        prefs.deleteKey(AppConstant.CREATED_ENQ_NUMBER)
        prefs.deleteKey(AppConstant.MAT_TYPE)
        prefs.deleteKey(AppConstant.TRUCK_TYPE)
        prefs.deleteKey(AppConstant.WEIGHT)
        prefs.deleteKey(AppConstant.DATE)
        prefs.deleteKey(AppConstant.INDENT_STATUS)
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


    override fun onResume() {
        super.onResume()
        mViewModel.callVersionCheck()
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
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}