package com.pojo.droptruck.di

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.pojo.droptruck.api.APIInterface
import com.pojo.droptruck.datastore.DataStorage
import com.pojo.droptruck.datastore.DataStorageImpl
import com.pojo.droptruck.utils.AppUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("API_LOG", message)
            }
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                /*.addHeader("token", TOKEN)
                .addHeader("language", "en")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")*/
                .addHeader("Accept", "application/json")
               // .addHeader("Authorization", "")
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor, interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(interceptor)
            .connectTimeout(45, TimeUnit.SECONDS) // connect timeout
            .readTimeout(45, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(AppUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): APIInterface {
        return retrofit.create(APIInterface::class.java)
    }

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext mContext: Context): DataStorage {
        return DataStorageImpl(mContext)
    }

}
