package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.adapter.photo.PhotoPagerView
import com.moban.model.data.Photo
import kotlinx.android.synthetic.main.item_feed_banner.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedBannerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(photos: List<Photo>) {
        val context = itemView.context
        val viewPagerAdapter = PhotoPagerView(context)
        viewPagerAdapter.setData(photos)
        itemView.feed_banner_view_pager.adapter = viewPagerAdapter
        itemView.feed_banner_view_pager.forceLayout()
        itemView.feed_banner_indicator.setupWithViewPager(itemView.feed_banner_view_pager, true)
    }
}
