package com.moban.mvp

import android.content.Context
import androidx.annotation.StringRes

/**
 * Created by thangnguyen on 12/16/17.
 */
interface BaseMvpView {

    fun getContext(): Context

    fun showError(error: String?)

    fun showError(@StringRes stringResId: Int)

    fun showMessage(@StringRes srtResId: Int)

    fun showMessage(message: String)

}
