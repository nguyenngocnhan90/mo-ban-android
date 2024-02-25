package com.moban.flow.projects.searchproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.KeywordProjectAdapter
import com.moban.adapter.project.SearchProjectAdapter
import com.moban.adapter.project.SuggestionProjectAdapter
import com.moban.enum.GACategory
import com.moban.enum.SearchForType
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.projects.booking.newbooking.NewBookingActivity
import com.moban.flow.projects.searchprojectadvance.SearchProjectAdvanceActivity
import com.moban.flow.projects.searchprojectresult.SearchProjectResultActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.handler.KeywordProjectItemHandler
import com.moban.handler.ProjectItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.deal.Promotion
import com.moban.model.data.project.Project
import com.moban.model.response.project.ListProject
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_search_project.*
import kotlinx.android.synthetic.main.search_suggestion_view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class SearchProjectActivity : BaseMvpActivity<ISearchProjectView, ISearchProjectPresenter>(), ISearchProjectView {
    override var presenter: ISearchProjectPresenter = SearchProjectPresenterIml()
    private var searchProjectAdapter: SearchProjectAdapter = SearchProjectAdapter()
    private var keywordProjectAdapter: KeywordProjectAdapter = KeywordProjectAdapter()
    private var suggestionProjectAdapter: SuggestionProjectAdapter = SuggestionProjectAdapter()
    private var pageSearch: Int = 1
    private var searchForType = SearchForType.NONE
    private var promotion: Promotion? = null

    companion object {
        private const val BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE"
        private const val BUNDLE_KEY_PROMOTION = "BUNDLE_KEY_PROMOTION"

        fun start(context: Context, searchForType: SearchForType = SearchForType.NONE, promotion: Promotion? = null) {
            val intent = Intent(context, SearchProjectActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY_SEARCH_TYPE, searchForType.value)
            promotion?.let {
                bundle.putSerializable(BUNDLE_KEY_PROMOTION, it)
            }
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search_project
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initRecycleView()
        initSearchBox()
        initKeyBoardListener()
        presenter.loadProject(pageSearch)

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_SEARCH_TYPE)) {
            searchForType = SearchForType.from(bundle?.getInt(BUNDLE_KEY_SEARCH_TYPE) ?: 0)
        }
        if (intent.hasExtra(BUNDLE_KEY_PROMOTION)) {
            promotion = bundle?.getSerializable(BUNDLE_KEY_PROMOTION) as Promotion
        }

        search_project_imgBack.setOnClickListener {
            finish()
        }

        search_project_view_search_advance.setOnClickListener {
            SearchProjectAdvanceActivity.start(this@SearchProjectActivity)
        }

        setGAScreenName("Search Project", GACategory.SEARCH)
    }

    private fun initKeyBoardListener() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(search_project_ed_search, InputMethodManager.SHOW_IMPLICIT)

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {
                search_project_btn_clear.visibility = View.VISIBLE
                search_project_view_search_advance.visibility = View.GONE
            }
        }
    }

    private fun initSearchBox() {
        search_project_ed_search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = search_project_ed_search.text.toString()

                    setVisibleKeyBoard(false)
                    LocalStorage.saveSearchKeywordProject(keyword)
                    reloadSearchHistory()

                    SearchProjectResultActivity.start(this, keyword, searchForType, promotion)
                }
            }
            true
        }

        search_project_ed_search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {
                val keyword = editable.toString()
                val isEmpty = keyword.isEmpty()

                search_suggestion_view_empty_keyword.visibility =
                        if (isEmpty) View.VISIBLE else View.GONE

                search_suggestion_view_keywords.visibility =
                        if (isEmpty) View.GONE else View.VISIBLE

                if (!isEmpty) {
                    val searchKeyword = getString(R.string.project_search_project_with_keyword) +
                            " \"" + keyword + "\""
                    search_suggestion_tv_keyword.text = searchKeyword

                    pageSearch = 1
                    presenter.searchProject(pageSearch, keyword)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(keyword: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        search_project_btn_clear.setOnClickListener {
            search_project_btn_clear.visibility = View.GONE
            search_project_view_search_advance.visibility = View.VISIBLE
            setVisibleKeyBoard(false)
            search_project_ed_search.setText("")
            search_suggestion_view_keywords.visibility = View.GONE
            search_suggestion_view_empty_keyword.visibility = View.VISIBLE
        }
    }

    private fun setVisibleKeyBoard(visible: Boolean) {
        if (!visible) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_project_ed_search.windowToken, 0)
        } else {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_project_ed_search.windowToken, 1)
        }
    }

    private fun onClickedResult(project: Project) {
        when (searchForType) {
            SearchForType.NONE -> ProjectDetailActivity.start(this, project)
            SearchForType.RESERVATION -> ListReservationActivity.start(this, project, isQuickDeal = true)
            SearchForType.BOOKING -> NewBookingActivity.start(this, project)
        }
    }

    private fun initRecycleView() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        search_suggestion_recycleView_projects_empty_keyword.layoutManager = gridLayoutManager
        search_suggestion_recycleView_projects_empty_keyword.adapter = suggestionProjectAdapter
        search_suggestion_recycleView_projects_empty_keyword.isNestedScrollingEnabled = false
        suggestionProjectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                onClickedResult(project)
            }

            override fun onFavorite(project: Project) {
            }
        }

        val linearKeywordLayoutManager = LinearLayoutManager(this)
        val recycleViewKeyword = search_suggestion_recycleView_keywords
        recycleViewKeyword.isNestedScrollingEnabled = false
        recycleViewKeyword.layoutManager = linearKeywordLayoutManager
        recycleViewKeyword.adapter = keywordProjectAdapter
        keywordProjectAdapter.listener = object : KeywordProjectItemHandler {
            override fun onClicked(keyword: String) {
                SearchProjectResultActivity.start(this@SearchProjectActivity, keyword, searchForType, promotion)
            }
        }
        reloadSearchHistory()

        val linearSuggestionProjectLayoutManager = LinearLayoutManager(this)
        val recycleViewSearchProject = search_suggestion_recycleView_projects
        recycleViewSearchProject.isNestedScrollingEnabled = false
        recycleViewSearchProject.layoutManager = linearSuggestionProjectLayoutManager
        recycleViewSearchProject.adapter = searchProjectAdapter
        searchProjectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                onClickedResult(project)
            }

            override fun onFavorite(project: Project) {
            }
        }
    }

    private fun reloadSearchHistory() {
        LocalStorage.searchKeywordProject()?.let {
            if (it.isNotEmpty()) {
                search_suggestion_keywords_history?.visibility = View.VISIBLE
                keywordProjectAdapter.keywords = it.toMutableList()
                keywordProjectAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun bindingProjects(listProject: ListProject, canLoadMore: Boolean) {
        suggestionProjectAdapter.projects.clear()
        suggestionProjectAdapter.appendProjects(listProject.list)
    }

    override fun bindingProjectsWithKeyword(listProject: ListProject, canLoadMore: Boolean) {
        searchProjectAdapter.projects.clear()
        pageSearch += 1

        searchProjectAdapter.setProjects(listProject.list, canLoadMore)
    }
}
