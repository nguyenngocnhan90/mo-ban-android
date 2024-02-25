package com.moban.dagger.module

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by thangnguyen on 12/18/17.
 */
@Module
class AppModule(application: Application) {
    val mApplication: Application

    init {
        mApplication = application
    }

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return mApplication
    }
}
