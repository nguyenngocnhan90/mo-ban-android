package com.moban.dagger.module

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.moban.helper.LocalStorage
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by LenVo on 04/09/18.
 */
@Module
class LinkmartNetModule(private var mBaseUrl: String) {
    private val username = "ck_f7ea7f6fc6ee235022bcba3c16b85b4f7ef15ac5"
    private val password = "cs_f9c603fe30570d4d95060642fd407258e64aea98"

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun provideHttpCache(application: Application): Cache {
        val cacheSize = 10L * 1024 * 1024
        return Cache(application.getCacheDir(), cacheSize)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.cache(cache)
        client.connectTimeout(20, TimeUnit.SECONDS)
        client.readTimeout(20, TimeUnit.SECONDS)
        client.writeTimeout(20, TimeUnit.SECONDS)
        val data = "$username:$password".toByteArray(charset("UTF-8"))
        val base64String = Base64.encodeToString(data, Base64.NO_WRAP)
        val basicAuth = "Basic $base64String".trim()

        LocalStorage.user()?.let {
            val interceptor = Interceptor { chain ->
                val url = chain.request().url()
                        .newBuilder()
                        .addQueryParameter("token", it.linkmart_token)
                        .build()
                val request = chain.request().newBuilder()
                        .url(url)
                        .addHeader("Authorization", basicAuth)
                        .build()
                chain.proceed(request)
            }

            client.addInterceptor(interceptor)
        }

        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build()
    }
}
