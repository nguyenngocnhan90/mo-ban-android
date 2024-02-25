package com.moban.adapter.rank

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.UserItemHandler
import com.moban.model.data.user.User
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.RankItemViewHolder

/**
 * Created by LenVo on 6/26/18.
 */
class TopPartnerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_RANK = 0
        private const val TYPE_LOAD_MORE = 1
    }
    private var isLoadMoreAvailable = false
    private var isLoading = false

    var loadMoreHandler: LoadMoreHandler? = null
    var listener: UserItemHandler? = null
    var isPartnerManager: Boolean = false

    var users: MutableList<User> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return when (viewType) {
            TYPE_RANK -> RankItemViewHolder(view)
            else -> LoadMoreViewHolder(view)
        }
    }

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_rank
    }

    override fun getItemViewType(position: Int): Int {
        if (position < users.count()) {
            return TYPE_RANK
        }

        return TYPE_LOAD_MORE
    }

    override fun getItemCount(): Int {
        return users.count() + if (isLoadMoreAvailable) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }
            else -> {
                if (position < users.count()) {
                    val user = users[position]

                    val viewHolder = holder as RankItemViewHolder
                    viewHolder.bind(user, position, isPartnerManager)
                    viewHolder.itemView.setOnClickListener {
                        listener?.onClicked(user)
                    }
                }
            }
        }
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setUsers(users: List<User>, isLoadMoreAvailable: Boolean) {
        this.users = users.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendUsers(users: List<User>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.users.size
        val length = users.size + if (isLoadMoreAvailable) 1 else 0

        this.users.addAll(users)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }
}
