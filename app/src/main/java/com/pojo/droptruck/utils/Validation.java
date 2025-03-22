package com.pojo.droptruck.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;

public class Validation {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        return !TextUtils.isEmpty(phone) && android.util.Patterns.PHONE.matcher(phone).matches() && phone.length()<=8;
    }

    public static boolean isValidText(String text) {
        return !TextUtils.isEmpty(text) ;
    }


    public static boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
