package com.moban.flow.secondary.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.adapter.secondary.SecondaryProjectAdapter
import com.moban.enum.GACategory
import com.moban.flow.secondary.create.simple.NewSimpleSecondaryActivity
import com.moban.flow.secondary.house.SecondaryHouseActivity
import com.moban.flow.secondary.search.SearchSecondaryProjectActivity
import com.moban.handler.ItemSecondaryMenuBottomHandler
import com.moban.handler.LoadMoreHandler
import com.moban.handler.SecondaryProjectItemHandler
import com.moban.model.data.secondary.SecondaryProject
import com.moban.model.response.secondary.project.ListSecondaryProject
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogBottomSheetUtil
import kotlinx.android.synthetic.main.activity_secondary_projects.*
import kotlinx.android.synthetic.main.nav.view.*

class SecondaryProjectsActivity : BaseMvpActivity<ISecondaryProjectView, ISecondaryProjectPresenter>(), ISecondaryProjectView {
    override var presenter: ISecondaryProjectPresenter = SecondaryProjectPresenterIml()
    private var page: Int = 1

    private val secondaryProjectAdapter: SecondaryProjectAdapter = SecondaryProjectAdapter()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SecondaryProjectsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_secondary_projects
    }

    override fun initialize(savedInstanceState: Bundle?) {
        secondary_project_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        secondary_project_nav.nav_tvSearchProject.visibility = View.VISIBLE
        secondary_project_nav.nav_tvSearchProject.text = getString(R.string.search_secondary)
        secondary_project_nav.nav_tvSearchProject.setOnClickListener {
            SearchSecondaryProjectActivity.start(this@SecondaryProjectsActivity)
        }

        secondary_project_nav.nav_add.visibility = View.VISIBLE
        secondary_project_nav.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogBottomSheetUtil.showDialogListActionSecondary(this, object: ItemSecondaryMenuBottomHandler {
                override fun onAddNewProject() {
                    dialog?.dismiss()
                    NewSimpleSecondaryActivity.start(this@SecondaryProjectsActivity)
                }
            })
        }
        initRecycleView()
        setGAScreenName("List Secondary", GACategory.SECONDARY)
    }

    private fun initRecycleView() {
        val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        secondary_project_recycler_view.layoutManager = gridLayoutManager
        secondary_project_recycler_view.adapter = secondaryProjectAdapter
        secondaryProjectAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadProjects(page)
            }
        }

        secondaryProjectAdapter.listener = object: SecondaryProjectItemHandler {
            override fun onClicked(project: SecondaryProject) {
                SecondaryHouseActivity.start(this@SecondaryProjectsActivity, project)
            }
        }

        secondary_project_refresh_layout.setOnRefreshListener {
            reloadData()
        }
        secondary_project_refresh_layout.isRefreshing = true
        reloadData()
    }

    private fun reloadData(forceClean: Boolean = false) {
        page = 1
        secondary_project_refresh_layout.isRefreshing = true
        if (forceClean) {
            secondaryProjectAdapter.clearSecondaryProjects()
        }
        presenter.loadProjects(page)
    }

    override fun bindingLoadSecondaryFailed() {
        secondary_project_refresh_layout.isRefreshing = false
    }

    override fun bindingSecondaryProject(response: ListSecondaryProject, canLoadMore: Boolean, total: Int) {
        secondary_project_refresh_layout.isRefreshing = false
        if (page == 1) {
            secondaryProjectAdapter.setListSecondaryProject(response.list, canLoadMore)
        } else {
            secondaryProjectAdapter.appendSecondaryProject(response.list, canLoadMore)
        }
    }
}
