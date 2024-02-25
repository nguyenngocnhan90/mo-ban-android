package com.moban.flow.rank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.moban.R
import com.moban.adapter.rank.TopPartnerAdapter
import com.moban.enum.GACategory
import com.moban.enum.PartnerFilterType
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.UserItemHandler
import com.moban.model.data.user.User
import com.moban.model.response.user.ListUsers
import com.moban.mvp.BaseMvpActivity
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_top_partner.*
import kotlinx.android.synthetic.main.nav.view.*

class TopPartnerActivity : BaseMvpActivity<ITopPartnerView, ITopPartnerPresenter>(), ITopPartnerView {
    override var presenter: ITopPartnerPresenter = TopPartnerPresenterIml()
    var pageArr = arrayOf(1, 1)
    val adapterArr = arrayOf(TopPartnerAdapter(), TopPartnerAdapter())
    val partnerType = arrayOf(PartnerFilterType.PARTNER, PartnerFilterType.PARTNER_MANAGER)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TopPartnerActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_top_partner
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initToolBar()
        initRecycleView()

        adapterArr.forEachIndexed{ index, _ ->
            presenter.loadTopPartner(pageArr[index], partnerType[index])
        }
        showTopPartner(0)
        setGAScreenName("Top Partner", GACategory.MORE)
    }

    private fun initRecycleView() {
        val linearLayoutArr = arrayOf(LinearLayoutManager(this),
                LinearLayoutManager(this), LinearLayoutManager(this))


        arrayOf(topPartner_recycler_view_partner, topPartner_recycler_view_manager)
                .forEachIndexed { index, view ->
                    view.layoutManager = linearLayoutArr[index]
                    view.adapter = adapterArr[index]
                    adapterArr[index].listener = object : UserItemHandler {
                        override fun onClicked(user: User) {
                            AccountDetailActivity.start(this@TopPartnerActivity, user.id)
                        }
                    }
                }

        adapterArr[1].isPartnerManager = true

        adapterArr.forEachIndexed {  index, topPartnerAdapter ->
            topPartnerAdapter.loadMoreHandler = object : LoadMoreHandler {
                override fun onLoadMore() {
                    presenter.loadTopPartner(pageArr[index], partnerType[index])
                }
            }
        }
    }

    private fun initToolBar() {
        topPartner_tbToolbar.nav_tvTitle.text = getString(R.string.top_partner)
        topPartner_tbToolbar.nav_imgBack.setOnClickListener {
            finish()
        }

        arrayOf(topPartner_tab_partner, topPartner_tab_manager)
                .forEachIndexed { index, view ->
                    view.setOnClickListener {
                        showTopPartner(index)
                    }
                }

        arrayOf(topPartner_refresh_partner, topPartner_refresh_manager)
                .forEachIndexed { index, view ->
                    view.setOnRefreshListener {
                        reloadTopPartner(index)
                    }
                }
    }

    override fun bindingTopPartner(listUsers: ListUsers, canLoadMore: Boolean, partnerType: PartnerFilterType) {
        val index = partnerType.ordinal
        val adapter = adapterArr[index]

        arrayOf(topPartner_refresh_partner, topPartner_refresh_manager)[index]?.isRefreshing = false
        if (adapter.users.isEmpty()) {
            adapter.setUsers(listUsers.list, canLoadMore)
        } else {
            adapter.appendUsers(listUsers.list, canLoadMore)
        }

        if (canLoadMore) {
            pageArr[index]++
        }
    }

    private fun reloadTopPartner(selectIndex: Int) {
        adapterArr[selectIndex].users.clear()
        pageArr[selectIndex] = 1
        presenter.loadTopPartner(pageArr[selectIndex], partnerType[selectIndex])
    }

    private fun showTopPartner(selectIndex: Int) {
        arrayOf(topPartner_separate_partner, topPartner_separate_manager)
                .forEachIndexed { index, view ->
                    view.visibility = if (index == selectIndex) View.VISIBLE else View.GONE
                }

        arrayOf(topPartner_tab_partner, topPartner_tab_manager)
                .forEachIndexed { index, view ->
                    val color = if (index == selectIndex) R.color.color_black else
                        R.color.color_black_50
                    view.setTextColor(Util.getColor(this, color))
                }

        arrayOf(topPartner_refresh_partner, topPartner_refresh_manager)
                .forEachIndexed { index, view ->
                    view.visibility = if (index == selectIndex) View.VISIBLE else View.GONE
                }
    }
}
