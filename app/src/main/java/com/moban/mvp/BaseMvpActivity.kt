package com.moban.mvp

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.moban.enum.GACategory
import io.github.inflationx.viewpump.ViewPumpContextWrapper

/**
 * Created by thangnguyen on 12/16/17.
 */
abstract class BaseMvpActivity<in V : BaseMvpView, T : BaseMvpPresenter<V>>
    : AppCompatActivity(), BaseMvpView {

    protected abstract var presenter: T

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this as V)

        setContentView(getLayoutId())

        initialize(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun getContext(): Context = this


    override fun showError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun showError(stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(srtResId: Int) {
        Toast.makeText(this, srtResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()

        System.gc()
        System.runFinalization()
    }

    /**
     *
     */

    /**
     * setup content layout
     *
     * @return layout id
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun initialize(savedInstanceState: Bundle?)

    protected fun setGAScreenName(screenName: String, inCategory: GACategory) {

    }

    protected fun sendGAEvent(action: String, category: String, label: String? = null) {

    }
}
