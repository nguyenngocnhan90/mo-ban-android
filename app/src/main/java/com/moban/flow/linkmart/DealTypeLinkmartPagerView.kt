package com.moban.flow.linkmart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.moban.R
import com.moban.adapter.linkmart.DealTypeAdapter
import com.moban.handler.DealTypeItemHandler
import com.moban.model.data.linkmart.LinkmartCategory
import kotlinx.android.synthetic.main.item_linkmart_type.view.*

class DealTypeLinkmartPagerView(context: Context) : PagerAdapter() {
    private var context: Context? = context
    private var categories: MutableList<LinkmartCategory> = ArrayList()
    private val MAX_ITEM_PER_PAGE = 4
    var listener: DealTypeItemHandler? = null

    override fun getCount(): Int {
        return categories.count()/MAX_ITEM_PER_PAGE + if (categories.count() % MAX_ITEM_PER_PAGE == 0)  0 else 1
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_linkmart_type, null)

        if (count == 0) {
            return view
        }
        val dealTypeAdapter = DealTypeAdapter()
        val itemCategories: MutableList<LinkmartCategory> = ArrayList()
        for(idx in position*MAX_ITEM_PER_PAGE until position*MAX_ITEM_PER_PAGE + MAX_ITEM_PER_PAGE) {
            if (idx in 0 until categories.count()) {
                itemCategories.add(categories[idx])
            }
        }
        val recycleView = view.item_linkmart_recycleView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycleView.layoutManager = layoutManager
        dealTypeAdapter.setCategoriesList(itemCategories)
        dealTypeAdapter.listener = listener
        recycleView.adapter = dealTypeAdapter

        val viewPager: ViewPager = container as ViewPager
        viewPager.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager: ViewPager = container as ViewPager
        val view: View = `object` as View
        viewPager.removeView(view)
    }

    fun setCategoriesList(categories: List<LinkmartCategory>) {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }
}
