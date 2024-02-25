package com.moban.flow.projectdeal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.moban.R
import com.moban.adapter.policy.PolicyDocumentAdapter
import com.moban.extension.toInt
import com.moban.model.data.document.Document
import com.moban.model.data.project.Project
import com.moban.model.response.BaseDataList
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_project_deals.*
import kotlinx.android.synthetic.main.nav.view.*

class ProjectDealsActivity : BaseMvpActivity<IProjectDealsView, IProjectDealsPresenter>(), IProjectDealsView {
    override var presenter: IProjectDealsPresenter = ProjectDealsPresenterIml()
    lateinit var project: Project
    private var pageDeals = 1
    private val dealAdapter = PolicyDocumentAdapter(PolicyDocumentAdapter.VERTICAL)
    private var isLoading = false

    override fun getLayoutId(): Int {
        return R.layout.activity_project_deals
    }

    companion object {
        private const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        fun start(context: Context, project: Project) {
            val intent = Intent(context, ProjectDealsActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)

            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        project_deal_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            return
        }

        project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        project_deal_nav.nav_tvTitle.text = project.product_Name

        initRecycleView()
        reloadData()
    }

    private fun reloadData() {
        project_deal_refresh_layout.isRefreshing = true
        pageDeals = 1
        presenter.getDeals(project.id, pageDeals)
    }

    private fun initRecycleView() {
        val layoutManager = GridLayoutManager(getContext(), 2)
        project_deal_recycler_view_deals.layoutManager = layoutManager

        dealAdapter.isSpecialDeal = true
        project_deal_recycler_view_deals.adapter = dealAdapter

        project_deal_scroll_view.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                        scrollY > oldScrollY) { //code to fetch more data for endless scrolling
                    if (!isLoading && dealAdapter.isLoadMoreAvailable) {
                        presenter.getDeals(project.id, pageDeals)
                        isLoading = true
                    }
                }
            }
        }
    }

    override fun bindingDealsList(deals: BaseDataList<Document>) {
        project_deal_refresh_layout.isRefreshing = false
        val total = deals.paging?.total_items ?: 0
        val totalDeals = getString(R.string.num_of_deals).replace("\$N", total.toString())
        project_deal_tv_num_deals.text = totalDeals

        val canLoadMore = deals.paging?.localCanLoadMore() ?: false
        val listDeals = deals.list
        if (pageDeals == 1) {
            dealAdapter.setDocumentList(listDeals, canLoadMore)
        } else {
            dealAdapter.appendProjects(listDeals, canLoadMore)
        }
        pageDeals += canLoadMore.toInt
        isLoading = false
    }
}
