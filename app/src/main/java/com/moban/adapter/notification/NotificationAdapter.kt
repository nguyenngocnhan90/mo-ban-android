package com.moban.adapter.notification

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.NotificationItemHandler
import com.moban.model.data.notification.Notification
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.NotificationItemViewHolder

/**
 * Created by LenVo on 5/11/18.
 */
class NotificationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_NOTIFICATION = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: NotificationItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var notifications: MutableList<Notification> = ArrayList()

    val isDataEmpty: Boolean
        get() = notifications.isEmpty()

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_notification
    }

    override fun getItemViewType(position: Int): Int {
        if (position < notifications.count()) {
            return TYPE_NOTIFICATION
        }
        return TYPE_LOAD_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)

        return when (viewType) {
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            else -> NotificationItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return notifications.count() + if (isLoadMoreAvailable) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }

            TYPE_NOTIFICATION -> {
                val viewHolder = holder as NotificationItemViewHolder

                if (position < notifications.count()) {
                    val project = notifications[position]
                    viewHolder.bind(project)

                    val itemView = holder.itemView

                    itemView.setOnClickListener {
                        listener?.onClicked(project)
                    }
                }
            }
        }
    }
    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setNotifications(notifications: List<Notification>, isLoadMoreAvailable: Boolean) {
        this.notifications = notifications.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendNotifications(notifications: List<Notification>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.notifications.size
        val length = notifications.size

        this.notifications.addAll(notifications)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun reloadPost(notification: Notification) {
        val index = notifications.indexOfFirst { p -> p.id == notification.id }

        if (index >= 0 && index < notifications.count()) {
            notifications[index] = notification
            notifyItemChanged(index)
        }
    }
}
