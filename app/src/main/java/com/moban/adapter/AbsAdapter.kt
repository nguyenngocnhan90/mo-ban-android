package com.moban.adapter

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.moban.handler.RecyclerItemListener

/**
 * Created by neal on 3/4/18.
 */
abstract class AbsAdapter<T: RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    var recyclerItemListener: RecyclerItemListener? = null

    override fun onBindViewHolder(holder: T, position: Int) {
        updateView(holder, position)
    }

    @LayoutRes
    abstract fun getLayoutResourceId(viewType: Int): Int

    abstract fun updateView(holder: T, position: Int)

}
