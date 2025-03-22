package com.pojo.droptruck


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.pojo.droptruck.utils.*;
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        prefs.save(AppConstant.VIEW_INDENT,"")

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // Code to be executed after a 1000ms (1-second) delay
            println("After delay")
            callNextPage()
        }, 1000)

    }

    private fun callNextPage() {
        try {
            val userId = prefs.getValueString(AppConstant.USER_ID)
            val role = prefs.getValueString(AppConstant.ROLE_ID)

            if (userId.isNullOrEmpty()) {
                LoginActivity()
            } else if (role.equals(AppConstant.CUSTOMER)){
                callCustomerActivity()
                //callNewMainActivity()
            }else{
                callNewMainActivity()
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }
}