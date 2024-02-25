package com.moban.flow.todaydeal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moban.R
import com.moban.adapter.feed.FeedDealAdapter
import com.moban.enum.GACategory
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.chart.ChartData
import com.moban.model.response.deal.ListDeals
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_today_deal.*
import kotlinx.android.synthetic.main.nav.view.*

class TodayDealActivity : BaseMvpActivity<ITodayDealView, ITodayDealPresenter>(), ITodayDealView {
    override var presenter: ITodayDealPresenter = TodayDealPresenterIml()
    private val todayDealAdapter: FeedDealAdapter = FeedDealAdapter()
    private var page: Int = 1

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TodayDealActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_today_deal
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initToolBar()
        initRecycleView()
        presenter.loadChartData()
        presenter.loadTodayDeals(page)
        setGAScreenName("Today Deal", GACategory.FEED)
    }



    private fun initToolBar() {
        today_deal_tbToolbar.nav_tvTitle.text = getString(R.string.top_partner)
        today_deal_tbToolbar.nav_imgBack.setOnClickListener {
            finish()
        }
    }

    private fun initRecycleView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        today_deal_recycleView.layoutManager = layoutManager
        today_deal_recycleView.adapter = todayDealAdapter
        todayDealAdapter.isIncludeChart = true
        todayDealAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadTodayDeals(page)
            }
        }
    }

    override fun bindingChartData(data: List<ChartData>) {
        todayDealAdapter.setDataChart(data)
    }

    override fun bindingTodayDeals(listDeals: ListDeals, canLoadMore: Boolean) {
        if (todayDealAdapter.deals.isEmpty()) {
            todayDealAdapter.setDeals(listDeals.list, canLoadMore)
        } else {
            todayDealAdapter.appendDeals(listDeals.list, canLoadMore)
        }

        if (canLoadMore) {
            page += 1
        }
    }
}
