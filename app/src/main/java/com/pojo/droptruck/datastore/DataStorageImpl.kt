package com.pojo.droptruck.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pojo.droptruck.utils.AppUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppUtils.PREFERENCE_NAME
)

class DataStorageImpl @Inject constructor(private val mContext: Context) : DataStorage {

    override suspend fun setString(key: String, value: String) {
        val preferenceKey = stringPreferencesKey(key)
        mContext.prefsDataStore.edit {
            it[preferenceKey] = value
        }
    }

    override suspend fun getString(key: String): String {
        return try {
            val preferenceKey = stringPreferencesKey(key)
            val value = mContext.prefsDataStore.data.first()
            value[preferenceKey].toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

    }

    override suspend fun clearKey(key: String) {
        val preferenceKey = stringPreferencesKey(key)
        mContext.prefsDataStore.edit {
            if (it.contains(preferenceKey)) {
                it.remove(preferenceKey)
            }
        }
    }

    override suspend fun removeAllKey() {
        mContext.prefsDataStore.edit {
            it.clear()
        }
    }

    fun data(key: String): Flow<String> {
        val preferenceKey = stringPreferencesKey(key)
        return mContext.prefsDataStore.data.map { preferences ->
            preferences[preferenceKey] ?: ""
        }
    }

}