package com.moban.adapter.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.moban.R
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.model.data.Photo
import com.moban.util.Device
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_photo_full_width.view.*

/**
 * Created by lenvo on 4/26/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class PhotoPagerView(context: Context) : PagerAdapter() {
    private var context: Context? = context
    private var photos: MutableList<Photo> = ArrayList()

    override fun getCount(): Int {
        return photos.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_photo_full_width, null)
        val photo = photos[position]
        LHPicasso.loadImage(photo.photo_Link, view.item_photo_img)

        val param = view.item_photo_full_width_card.layoutParams
        param.height = Device.getScreenWidth() * 9 / 16
        view.item_photo_full_width_card.layoutParams = param

        val context = view.context
        view.setOnClickListener {
            PostDetailActivity.start(context, photo.id)
        }
        val viewPager: ViewPager = container as ViewPager
        viewPager.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager: ViewPager = container as ViewPager
        val view: View = `object` as View
        viewPager.removeView(view)
    }

    fun setData(photos: List<Photo>) {
        this.photos.clear()
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }
}
