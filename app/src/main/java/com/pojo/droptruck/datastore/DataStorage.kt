package com.pojo.droptruck.datastore

interface DataStorage {

    suspend fun setString(key: String, value: String)
    suspend fun getString(key: String): String
    suspend fun clearKey(key: String)
    suspend fun removeAllKey()

}