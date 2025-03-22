package com.pojo.droptruck.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper (val context: Context) {

    private val PREF_NAME = "DT"
    val sharedPreferences : SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun save(key : String, text: String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, text)
        editor.apply()
    }

    fun saveInt(key : String, value: Int){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun saveLong(key : String, value: Long){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getValueString(key: String) : String?{
        return sharedPreferences.getString(key, "")
    }

    fun getIntValue(key:String): Int {
        return sharedPreferences.getInt(key,0)
    }
    fun getLongValue(key:String): Long {
        return sharedPreferences.getLong(key,0)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun deleteKey(key:String) {
        sharedPreferences.edit().remove(key).apply()
    }

}