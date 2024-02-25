package com.moban.adapter.booking

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectBookingItemHandler
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Project
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.ProjectBookingItemViewHolder

/**
 * Created by LenVo on 3/21/18.
 */
class ProjectBookingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_BOOKING = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: ProjectBookingItemHandler? = null

    var projectBookings: MutableList<ProjectBooking> = ArrayList()

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var project: Project? = null

    override fun getItemCount(): Int {
        return projectBookings.count() + if (isLoadMoreAvailable) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)

        return when (viewType) {
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            else -> ProjectBookingItemViewHolder(view)
        }
    }

    fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_booking
    }

    override fun getItemViewType(position: Int): Int {
        if (position < projectBookings.count()) {
            return TYPE_BOOKING
        }

        return TYPE_LOAD_MORE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }

            TYPE_BOOKING -> {
                val viewHolder = holder as ProjectBookingItemViewHolder

                if (position < projectBookings.count()) {
                    val projectBooking = projectBookings[position]
                    viewHolder.bind(projectBooking, project)

                    viewHolder.itemView.setOnClickListener {
                        //            if (!projectBooking.isOutOfTime()) {
                        listener?.onClicked(projectBooking)
            //            }
                    }
                }
            }
        }
    }

    fun appendBookings(bookings: List<ProjectBooking>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.projectBookings.size
        val length = bookings.size

        this.projectBookings.addAll(bookings)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setBookings(bookings: List<ProjectBooking>, mIsLoadMoreAvailable: Boolean) {
        this.projectBookings = bookings.toMutableList()
        this.isLoadMoreAvailable = mIsLoadMoreAvailable

        notifyDataChanged()
    }
}
