package com.moban.flow.projects.searchprojectresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.ProjectAdapter
import com.moban.enum.GACategory
import com.moban.enum.SearchForType
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.projects.booking.newbooking.NewBookingActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectItemHandler
import com.moban.model.data.deal.Promotion
import com.moban.model.data.project.Project
import com.moban.model.param.SearchAdvanceParams
import com.moban.model.response.project.ListProject
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_search_project_result.*
import kotlinx.android.synthetic.main.nav.view.*

class SearchProjectResultActivity :
    BaseMvpActivity<ISearchProjectResultView, ISearchProjectResultPresenter>(),
    ISearchProjectResultView {
    override var presenter: ISearchProjectResultPresenter = SearchProjectResultPresenterIml()

    private var pageSearch: Int = 1
    private var projectAdapter: ProjectAdapter = ProjectAdapter()
    private var keyword: String = ""
    private var searchAdvanceParam: SearchAdvanceParams? = null
    private var searchForType = SearchForType.NONE
    private var promotion: Promotion? = null

    companion object {
        const val BUNDLE_KEY_KEYWORD = "BUNDLE_KEY_KEYWORD"
        const val BUNDLE_KEY_ADVANCE_PARAMS = "BUNDLE_KEY_ADVANCE_PARAMS"
        private const val BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE"
        private const val BUNDLE_KEY_PROMOTION = "BUNDLE_KEY_PROMOTION"

        fun start(
            context: Context,
            keywords: String,
            searchForType: SearchForType = SearchForType.NONE,
            promotion: Promotion? = null
        ) {
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY_KEYWORD, keywords)
            bundle.putInt(BUNDLE_KEY_SEARCH_TYPE, searchForType.value)
            promotion?.let {
                bundle.putSerializable(BUNDLE_KEY_PROMOTION, it)
            }

            val intent = Intent(context, SearchProjectResultActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }

        fun startAdvance(
            context: Context,
            searchAdvanceParams: SearchAdvanceParams,
            searchForType: SearchForType = SearchForType.NONE
        ) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_ADVANCE_PARAMS, searchAdvanceParams)
            bundle.putInt(BUNDLE_KEY_SEARCH_TYPE, searchForType.value)

            val intent = Intent(context, SearchProjectResultActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_search_project_result
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_KEYWORD)) {
            keyword = bundle?.getString(BUNDLE_KEY_KEYWORD) ?: ""
            val searchText = getString(R.string.search) + ": " + keyword
            search_project_nav.nav_tvTitle.text = searchText

            presenter.searchProjectWithKeyword(pageSearch, keyword)
        } else {
            if (intent.hasExtra(BUNDLE_KEY_ADVANCE_PARAMS)) {
                search_project_nav.nav_tvTitle.text = getString(R.string.search_project_advance)
                searchAdvanceParam = bundle?.get(BUNDLE_KEY_ADVANCE_PARAMS) as SearchAdvanceParams
                searchAdvanceParam?.let {
                    presenter.searchAdvance(it)
                }
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_SEARCH_TYPE)) {
            searchForType = SearchForType.from(bundle?.getInt(BUNDLE_KEY_SEARCH_TYPE) ?: 0)
        }
        if (intent.hasExtra(BUNDLE_KEY_PROMOTION)) {
            promotion = bundle?.getSerializable(BUNDLE_KEY_PROMOTION) as Promotion
        }

        search_project_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        initRecyclerView()
        setGAScreenName("Search Result", GACategory.SEARCH)
    }

    private fun initRecyclerView() {

        val linearLayoutManager = LinearLayoutManager(this)
        search_project_recycler_view.layoutManager = linearLayoutManager
        search_project_recycler_view.isNestedScrollingEnabled = false

        projectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                onClickedResult(project)
            }

            override fun onFavorite(project: Project) {

            }
        }

        projectAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.searchProjectWithKeyword(pageSearch, keyword)
            }
        }

        search_project_recycler_view.adapter = projectAdapter
    }

    private fun onClickedResult(project: Project) {
        when (searchForType) {
            SearchForType.NONE -> ProjectDetailActivity.start(this, project)
            SearchForType.RESERVATION -> ListReservationActivity.start(this, project, isQuickDeal = true)
            SearchForType.BOOKING -> NewBookingActivity.start(this, project)
        }
    }

    override fun bindingProject(listProject: ListProject, canLoadMore: Boolean) {
        listProject.paging?.let {
            val searchResult =
                it.total_items.toString() + " " + getString(R.string.project_search_result)
            search_project_tv_result.text = searchResult
        }

        if (projectAdapter.projects.isEmpty()) {
            projectAdapter.setProjects(listProject.list, canLoadMore)
        } else {
            projectAdapter.appendProjects(listProject.list, canLoadMore)
        }

        if (canLoadMore) {
            pageSearch += 1
        }
    }
}
