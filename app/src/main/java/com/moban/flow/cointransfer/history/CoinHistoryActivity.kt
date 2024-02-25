package com.moban.flow.cointransfer.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moban.R
import com.moban.adapter.user.CoinTransactionAdapter
import com.moban.adapter.user.LinkPointTransactionAdapter
import com.moban.enum.GACategory
import com.moban.flow.cointransfer.detail.CoinTransactionActivity
import com.moban.handler.CoinTransactionItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.user.LhcTransaction
import com.moban.model.response.coin.ListLinkPointTransactions
import com.moban.model.response.coin.ListTransactions
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_coin_history.*
import kotlinx.android.synthetic.main.nav.view.*

class CoinHistoryActivity : BaseMvpActivity<ICoinHistoryView, ICoinHistoryPresenter>(), ICoinHistoryView {
    override var presenter: ICoinHistoryPresenter = CoinHistoryPresenterIml()
    private var page: Int = 1
    private val transactionAdapter = CoinTransactionAdapter()
    private var isLinkPoint: Boolean = false
    private val linkpointAdapter = LinkPointTransactionAdapter()


    override fun getLayoutId(): Int {
        return R.layout.activity_coin_history
    }

    companion object {
        const val BUNDLE_KEY_LINKPOINT = "BUNDLE_KEY_LINKPOINT"

        fun start(context: Context, isLinkPoint: Boolean = false) {
            val intent = Intent(context, CoinHistoryActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_LINKPOINT, isLinkPoint)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Coin History", GACategory.HOME)

        val bundle = intent.extras

        if (intent.hasExtra(BUNDLE_KEY_LINKPOINT)) {
            isLinkPoint = bundle?.getSerializable(BUNDLE_KEY_LINKPOINT) as Boolean
        }

        lixi_history_nav.nav_tvTitle.text = if (isLinkPoint) getString(R.string.linkpoint_history) else
            getString(R.string.lixi_history)

        lixi_history_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        lixi_history_refresh_layout.setOnRefreshListener {
            lixi_history_refresh_layout.isRefreshing = true
            reloadHistory()
        }

        initRecycleView()
        reloadHistory()
    }


    private fun initRecycleView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        lixi_history_recycler_view.layoutManager = layoutManager
        lixi_history_recycler_view.adapter = if (isLinkPoint) linkpointAdapter else transactionAdapter

        transactionAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadTransactions(++page)
            }
        }

        transactionAdapter.listener = object: CoinTransactionItemHandler {
            override fun onClicked(lhcTransaction: LhcTransaction) {
                CoinTransactionActivity.start(this@CoinHistoryActivity, lhcTransaction)
            }
        }

        linkpointAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadLinkPointTransactions(++page)
            }
        }
    }

    private fun reloadHistory() {
        page = 1
        if (isLinkPoint) {
            presenter.loadLinkPointTransactions(page)
        } else {
            presenter.loadTransactions(page)
        }
    }

    override fun bindingTransactions(transactions: ListTransactions) {
        lixi_history_refresh_layout.isRefreshing = false

        var canLoadMore = false
        transactions.paging?.let {
            canLoadMore = page < it.total
        }
        transactions.paging?.let {
            canLoadMore = page < it.total
        }
        if (page == 1) {
            transactionAdapter.setTransactions(transactions.list, canLoadMore)
        } else {
            transactionAdapter.appendTransactions(transactions.list, canLoadMore)
        }
    }

    override fun bindingLinkPointTransactions(transactions: ListLinkPointTransactions) {
        lixi_history_refresh_layout.isRefreshing = false

        var canLoadMore = false
        transactions.paging?.let {
            canLoadMore = page < it.total
        }

        if (page == 1) {
            linkpointAdapter.setTransactions(transactions.list, canLoadMore)
        } else {
            linkpointAdapter.appendTransactions(transactions.list, canLoadMore)
        }
    }
}
