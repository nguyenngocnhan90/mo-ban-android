package com.moban.mvp


import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Created by neal on 3/4/18.
 */

abstract class BaseMvpFragment<in V: BaseMvpView, T : BaseMvpPresenter<V>> : Fragment(), BaseMvpView {

    protected abstract var presenter: T

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this as V)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun getContext(): Context {
        return activity as Context
    }

    override fun showError(error: String?) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    override fun showError(stringResId: Int) {
        Toast.makeText(context, stringResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(srtResId: Int) {
        Toast.makeText(context, srtResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()

        System.gc()
        System.runFinalization()
    }

    /**
     * setup content layout
     *
     * @return layout id
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun initialize(savedInstanceState: Bundle?)
}
