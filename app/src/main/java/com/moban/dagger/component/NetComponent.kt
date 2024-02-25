package com.moban.dagger.component

import com.moban.dagger.module.AppModule
import com.moban.dagger.module.NetModule
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton


/**
 * Created by thangnguyen on 12/18/17.
 */
@Singleton
@Component(modules = [AppModule::class, NetModule::class])
interface NetComponent {
    // downstream components need these exposed with the return type
    // method name does not really matter
    fun retrofit(): Retrofit
}
