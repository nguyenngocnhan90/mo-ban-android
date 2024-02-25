package com.moban.dagger.component

import com.moban.dagger.module.AppModule
import com.moban.dagger.module.LinkmartNetModule
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton


/**
 * Created by LenVo on 04/09/18.
 */
@Singleton
@Component(modules = [AppModule::class, LinkmartNetModule::class])
interface LinkmartNetComponent {
    // downstream components need these exposed with the return type
    // method name does not really matter
    fun retrofit(): Retrofit
}
