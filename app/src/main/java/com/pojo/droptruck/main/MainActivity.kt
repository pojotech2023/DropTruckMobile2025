package com.pojo.droptruck.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pojo.droptruck.R

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.pojo.droptruck.databinding.ActivityMainBinding
//import com.pojo.droptruck.utils.callHomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var navController: NavController? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    //https://www.youtube.com/watch?v=hV8d3zfa5V0
    var navHost: NavHostFragment? = null
    var mBinding: ActivityMainBinding? = null
    var profileLayout: RelativeLayout? = null
    var enq_lay: RelativeLayout? = null
    var bd_lay: RelativeLayout? = null
    var ph_lay: RelativeLayout? = null
    var settings_lay: RelativeLayout? = null

    var page_title: TextView? = null
    var mImgMenu: ImageView? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        profileLayout = findViewById(R.id.profile_lay)
        //enq_lay = findViewById(R.id.enq_lay)
        bd_lay = findViewById(R.id.bd_lay)
        //ph_lay = findViewById(R.id.ph_lay)
        //settings_lay = findViewById(R.id.settings_lay)
        page_title = findViewById(R.id.page_title)
        mImgMenu = findViewById(R.id.img_menu)

        //New
        navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment?
        navController = navHost!!.navController
        val navInflater = navController!!.navInflater
        graph = navInflater.inflate(R.navigation.nav_graph)
            //graph!!.setStartDestination(R.id.createIntentFragment)
        navController!!.graph = graph!!
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationViewDrawer)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close)
        navigationView!!.setNavigationItemSelectedListener(this)

        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        setupWithNavController(navigationView!!, navController!!)

        profileLayout!!.setOnClickListener { openPage(R.id.dashboardFragment,"Dashboard") }
        enq_lay!!.setOnClickListener {
            drawerLayout!!.closeDrawers()
            //openPage(R.id.enquiryFragment,"Enquiry")
        }
        //bd_lay!!.setOnClickListener { openPage(R.id.createIntentFragment,"Create Intent") }
        ph_lay!!.setOnClickListener {
            //callHomeActivity()
        }
        //settings_lay!!.setOnClickListener { openPage(R.id.drawerSettings,"Settings") }

        mImgMenu?.setOnClickListener {
            drawerLayout?.openDrawer(Gravity.START)
        }

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

}