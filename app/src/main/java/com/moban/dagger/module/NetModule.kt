package com.moban.dagger.module

import android.app.Application
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import com.google.gson.Gson
import javax.inject.Singleton
import dagger.Provides
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import android.preference.PreferenceManager
import android.content.SharedPreferences
import com.moban.LHApplication
import com.moban.constant.ServerURL
import com.moban.flow.signin.SignInActivity
import com.moban.helper.LocalStorage
import com.moban.util.Device
import dagger.Module
import okhttp3.Cache
import okhttp3.Interceptor
import java.util.concurrent.TimeUnit


/**
 * Created by thangnguyen on 12/18/17.
 */
@Module
class NetModule(private var mBaseUrl: String, private var application: LHApplication) {

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
        client.connectTimeout(20, TimeUnit.SECONDS)
        client.readTimeout(20, TimeUnit.SECONDS)
        client.writeTimeout(20, TimeUnit.SECONDS)
        client.cache(cache)

        val appVersion = Device.appVersion(LHApplication.instance.applicationContext)

        val token = LocalStorage.token()
        if (!token.isEmpty()) {
            val interceptor = Interceptor { chain ->
                val request = chain.request()?.newBuilder()?.
                        addHeader("token", token)?.
                        addHeader("app_version", appVersion)?.build()
                val response = chain.proceed(request)
                if (response.code() == ServerURL.SERVER_CODE_UNAUTHENTICATED) {
                    SignInActivity.start(application)
                }
                response
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
