package com.moban.flow.homedeal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.photo.DocumentPagerView
import com.moban.adapter.policy.PolicyDocumentAdapter
import com.moban.adapter.project.ProjectPhotoAdapter
import com.moban.extension.toInt
import com.moban.flow.projectdeal.ProjectDealsActivity
import com.moban.handler.ProjectPhotoItemHandler
import com.moban.model.data.document.Document
import com.moban.model.data.homedeal.HomeDeal
import com.moban.model.data.homedeal.HomeDealDetail
import com.moban.model.data.project.Project
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_home_deal.*
import kotlinx.android.synthetic.main.nav.view.*


class HomeDealActivity : BaseMvpActivity<IHomeDealView, IHomeDealPresenter>(), IHomeDealView {
    override var presenter: IHomeDealPresenter = HomeDealPresenterIml()
    private var typeId = ""
    private lateinit var homeDeal: HomeDeal
    private var pageDeals = 1
    private val dealAdapter = PolicyDocumentAdapter(PolicyDocumentAdapter.VERTICAL)
    private val viewPagerAdapter = DocumentPagerView(this)
    private var isLoading = false

    private val photoAdapter = ProjectPhotoAdapter()

    companion object {
        private const val BUNDLE_KEY_HOME_DEAL = "BUNDLE_KEY_HOME_DEAL"

        fun start(context: Context, homeDeal: HomeDeal) {
            val intent = Intent(context, HomeDealActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_HOME_DEAL, homeDeal)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_deal
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        home_deal_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        if (!intent.hasExtra(BUNDLE_KEY_HOME_DEAL)) {
            return
        }
        homeDeal = bundle?.getSerializable(BUNDLE_KEY_HOME_DEAL) as HomeDeal

        home_deal_nav.nav_tvTitle.text = homeDeal.title
        typeId = homeDeal.type

        initRecycleView()
        initPagerView()

        home_deal_refresh_layout.setOnRefreshListener {
            reloadData()
        }
        reloadData()
    }

    private fun initPagerView() {
        home_deal_view_pager.adapter = viewPagerAdapter
        home_deal_view_pager.forceLayout()
        home_deal_indicator.setupWithViewPager(home_deal_view_pager, true)
    }

    private fun initRecycleView() {
        val layoutManager = GridLayoutManager(getContext(), 2)
        home_deal_recycler_view_deals.layoutManager = layoutManager
        home_deal_recycler_view_deals.adapter = dealAdapter

        home_deal_scroll_view.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                        scrollY > oldScrollY) { //code to fetch more data for endless scrolling
                    if (!isLoading && dealAdapter.isLoadMoreAvailable) {
                        presenter.getListDeals(typeId, pageDeals)
                        isLoading = true
                    }
                }
            }
        }

        val projectLayout = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        home_deal_recycle_view_project.layoutManager = projectLayout
        home_deal_recycle_view_project.adapter = photoAdapter
        photoAdapter.listener = object : ProjectPhotoItemHandler {
            override fun onClicked(photo: Project, pos: Int) {
                ProjectDealsActivity.start(this@HomeDealActivity, photo)
            }
        }
    }

    private fun reloadData() {
        home_deal_refresh_layout.isRefreshing = true
        pageDeals = 1
        presenter.get(typeId)
    }

    override fun bindingDealDetail(dealDetail: HomeDealDetail) {
        home_deal_refresh_layout.isRefreshing = false

        //Project Image:
        val projects = dealDetail.projects
        photoAdapter.setPhotosList(projects)

        //Banner
        val banners = dealDetail.banners
        viewPagerAdapter.setData(banners)

        //Deals
        val deals = dealDetail.deals
        val canLoadMore = deals.paging?.localCanLoadMore() ?: false
        dealAdapter.setDocumentList(deals.list, canLoadMore)
        pageDeals += canLoadMore.toInt

        val total = dealDetail.deals.paging?.total_items ?: 0
        val totalDeals = getString(R.string.num_of_deals).replace("\$N", total.toString())
        home_deal_tv_num_deals.text = totalDeals
    }

    override fun bindingDealsList(deals: List<Document>, canLoadMore: Boolean) {
        dealAdapter.appendProjects(deals, canLoadMore)
        pageDeals += canLoadMore.toInt
        isLoading = false
    }
}
