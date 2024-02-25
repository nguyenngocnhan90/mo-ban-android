package com.moban.flow.secondary.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.moban.R
import com.moban.adapter.project.KeywordProjectAdapter
import com.moban.adapter.secondary.SearchSecondaryProjectAdapter
import com.moban.adapter.secondary.SuggestionSecondaryProjectAdapter
import com.moban.enum.GACategory
import com.moban.flow.secondary.detail.SecondaryProjectDetailActivity
import com.moban.flow.secondary.searchresult.SearchSecondaryProjectResultActivity
import com.moban.handler.KeywordProjectItemHandler
import com.moban.handler.SecondaryHouseItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.model.response.secondary.ListSecondaryHouse
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_search_project.*
import kotlinx.android.synthetic.main.nav.view.*
import kotlinx.android.synthetic.main.search_suggestion_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import kotlin.coroutines.CoroutineContext

class SearchSecondaryProjectActivity : BaseMvpActivity<ISearchSecondaryProjectView, ISearchSecondaryProjectPresenter>(), ISearchSecondaryProjectView, CoroutineScope {
    override var presenter: ISearchSecondaryProjectPresenter = SearchSecondaryProjectPresenterIml()
    private var searchProjectAdapter: SearchSecondaryProjectAdapter = SearchSecondaryProjectAdapter()
    private var keywordProjectAdapter: KeywordProjectAdapter = KeywordProjectAdapter()
    private var suggestionProjectAdapter: SuggestionSecondaryProjectAdapter = SuggestionSecondaryProjectAdapter()
    private var pageSearch: Int = 1
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SearchSecondaryProjectActivity::class.java)
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
        presenter.loadHighlightProject()

        search_project_imgBack.setOnClickListener {
            finish()
        }

        search_project_ed_search.hint = getString(R.string.search_secondary)
        search_suggestion_tv_favorite_project.text = getString(R.string.favorite_secondary)
        setGAScreenName("Search Secondary", GACategory.SECONDARY)
    }

    private fun initKeyBoardListener() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(search_project_ed_search, InputMethodManager.SHOW_IMPLICIT)

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {
                search_project_btn_clear.visibility = View.VISIBLE
            }
        }
    }

    private fun initSearchBox() {
        val watcher = object :TextWatcher{
            private var searchFor = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor)
                    return

                searchFor = searchText

                launch {
                    delay(300)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch

                    val isEmpty = searchText.isEmpty()

                    search_suggestion_view_empty_keyword.visibility =
                            if (isEmpty) View.VISIBLE else View.GONE

                    search_suggestion_view_keywords.visibility =
                            if (isEmpty) View.GONE else View.VISIBLE

                    if (!isEmpty) {
                        val searchKeyword = getString(R.string.project_search_project_with_keyword) +
                                " \"" + searchText + "\""
                        search_suggestion_tv_keyword.text = searchKeyword

                        pageSearch = 1
                        presenter.searchProject(pageSearch, searchText)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        }

        search_project_ed_search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = search_project_ed_search.text.toString()

                    setVisibleKeyBoard(false)
                    LocalStorage.saveSearchKeywordSecondaryProject(keyword)
                    reloadSearchHistory()

                    SearchSecondaryProjectResultActivity.start(this, keyword)
                }
            }
            true
        }

        search_project_ed_search.addTextChangedListener(watcher)

        search_project_btn_clear.setOnClickListener {
            search_project_btn_clear.visibility = View.INVISIBLE
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

    private fun initRecycleView() {
        val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        search_suggestion_recycleView_projects_empty_keyword.layoutManager = gridLayoutManager
        search_suggestion_recycleView_projects_empty_keyword.adapter = suggestionProjectAdapter
        search_suggestion_recycleView_projects_empty_keyword.isNestedScrollingEnabled = false
        suggestionProjectAdapter.listener = object : SecondaryHouseItemHandler {
            override fun onClicked(house: SecondaryHouse) {
                SecondaryProjectDetailActivity.start(this@SearchSecondaryProjectActivity, house)
            }
        }

        val linearKeywordLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val recycleViewKeyword = search_suggestion_recycleView_keywords
        recycleViewKeyword.isNestedScrollingEnabled = false
        recycleViewKeyword.layoutManager = linearKeywordLayoutManager
        recycleViewKeyword.adapter = keywordProjectAdapter
        keywordProjectAdapter.listener = object : KeywordProjectItemHandler {
            override fun onClicked(keyword: String) {
                SearchSecondaryProjectResultActivity.start(this@SearchSecondaryProjectActivity, keyword)
            }
        }
        reloadSearchHistory()

        val linearSuggestionProjectLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val recycleViewSearchProject = search_suggestion_recycleView_projects
        recycleViewSearchProject.isNestedScrollingEnabled = false
        recycleViewSearchProject.layoutManager = linearSuggestionProjectLayoutManager
        recycleViewSearchProject.adapter = searchProjectAdapter
        searchProjectAdapter.listener = object : SecondaryHouseItemHandler {
            override fun onClicked(house: SecondaryHouse) {
                SecondaryProjectDetailActivity.start(this@SearchSecondaryProjectActivity, house)
            }
        }
    }

    private fun reloadSearchHistory() {
        LocalStorage.searchKeywordSecondaryProject()?.let {
            if (it.isNotEmpty()) {
                search_suggestion_keywords_history?.visibility = View.VISIBLE
                keywordProjectAdapter.keywords = it.toMutableList()
                keywordProjectAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun bindingHighlightProjects(listHouse: ListSecondaryHouse) {
        suggestionProjectAdapter.houses.clear()
        suggestionProjectAdapter.appendProjects(listHouse.list)
    }

    override fun bindingProjectsWithKeyword(listHouse: ListSecondaryHouse, canLoadMore: Boolean) {
        searchProjectAdapter.houses.clear()
        pageSearch += 1

        searchProjectAdapter.setProjects(listHouse.list, canLoadMore)
    }
}
