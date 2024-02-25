package com.moban.flow.gifts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.gift.GiftsAdapter
import com.moban.enum.GACategory
import com.moban.handler.GiftItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.gift.Gift
import com.moban.model.response.gift.ListGifts
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_gifts.*
import kotlinx.android.synthetic.main.nav.view.*

class GiftsActivity : BaseMvpActivity<IGiftsView, IGiftsPresenter>(), IGiftsView {
    override var presenter: IGiftsPresenter = GiftsPresenterIml()

    private var page: Int = 1
    private val giftsAdapter: GiftsAdapter = GiftsAdapter()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, GiftsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_gifts
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Linkmart Gift", GACategory.ACCOUNT)
        gifts_nav.nav_tvTitle.text = getText(R.string.link_mart)
        gifts_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        initRecycleView()
        presenter.loadGifts(page)
    }

    private fun initRecycleView() {
        val layoutManager = GridLayoutManager(getContext(), 2)
        gifts_recycle_view.layoutManager = layoutManager

        gifts_recycle_view.adapter = giftsAdapter
        gifts_recycle_view.isNestedScrollingEnabled = false

        giftsAdapter.listener = object : GiftItemHandler {
            override fun onClicked(deal: Gift) {
                //TODO: View gift detail
            }
        }

        giftsAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadGifts(page)
            }
        }
    }

    override fun bindingGifts(listGifts: ListGifts, canLoadMore: Boolean) {
        if (giftsAdapter.gifts.isEmpty()) {
            giftsAdapter.setGifts(listGifts.list, canLoadMore)
        } else {
            giftsAdapter.appendGifts(listGifts.list, canLoadMore)
        }

        if (canLoadMore) {
            page += 1
        }

        gifts_recycle_view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val layoutParams = gifts_recycle_view.layoutParams
        layoutParams.height = gifts_recycle_view.measuredHeight
        gifts_recycle_view.layoutParams = layoutParams
    }
}
