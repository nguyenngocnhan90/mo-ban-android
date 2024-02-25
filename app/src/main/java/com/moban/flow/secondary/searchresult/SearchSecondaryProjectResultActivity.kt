package com.moban.flow.secondary.searchresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moban.R
import com.moban.adapter.secondary.SecondaryHouseAdapter
import com.moban.enum.GACategory
import com.moban.flow.secondary.detail.SecondaryProjectDetailActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.SecondaryHouseItemHandler
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.model.param.SearchAdvanceParams
import com.moban.model.response.secondary.ListSecondaryHouse
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_search_project_result.*
import kotlinx.android.synthetic.main.nav.view.*

class SearchSecondaryProjectResultActivity : BaseMvpActivity<ISearchSecondaryProjectResultView, ISearchSecondaryProjectResultPresenter>(),
        ISearchSecondaryProjectResultView {
    override var presenter: ISearchSecondaryProjectResultPresenter = SearchSecondaryProjectResultPresenterIml()

    private var pageSearch: Int = 1
    private var houseAdapter: SecondaryHouseAdapter = SecondaryHouseAdapter()
    private var keyword: String = ""
    private var searchAdvanceParam: SearchAdvanceParams? = null

    companion object {
        const val BUNDLE_KEY_KEYWORD = "BUNDLE_KEY_KEYWORD"
        const val BUNDLE_KEY_ADVANCE_PARAMS = "BUNDLE_KEY_ADVANCE_PARAMS"

        fun start(context: Context, keywords: String) {
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY_KEYWORD, keywords)

            val intent = Intent(context, SearchSecondaryProjectResultActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }

        fun startAdvance(context: Context, searchAdvanceParams: SearchAdvanceParams) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_ADVANCE_PARAMS, searchAdvanceParams)

            val intent = Intent(context, SearchSecondaryProjectResultActivity::class.java)
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

        search_project_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        initRecyclerView()
        setGAScreenName("Search Secondary Result - keyword: $keyword", GACategory.SECONDARY)
    }
    
    private fun initRecyclerView() {

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        search_project_recycler_view.layoutManager = linearLayoutManager
        search_project_recycler_view.isNestedScrollingEnabled = false

        houseAdapter.listener = object : SecondaryHouseItemHandler {
            override fun onClicked(house: SecondaryHouse) {
                SecondaryProjectDetailActivity.start(this@SearchSecondaryProjectResultActivity, house)
            }
        }

        houseAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.searchProjectWithKeyword(pageSearch, keyword)
            }
        }

        search_project_recycler_view.adapter = houseAdapter
    }

    override fun bindingProject(listHouse: ListSecondaryHouse, canLoadMore: Boolean) {
        listHouse.paging?.let {
            val searchResult = it.total_items.toString() + " " + getString(R.string.project_search_result)
            search_project_tv_result.text = searchResult
        }

        if (houseAdapter.houses.isEmpty()) {
            houseAdapter.setListSecondaryProject(listHouse.list, canLoadMore)
        } else {
            houseAdapter.appendSecondaryProject(listHouse.list, canLoadMore)
        }

        if (canLoadMore) {
            pageSearch += 1
        }
    }
}
