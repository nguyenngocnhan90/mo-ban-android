package com.moban.flow.projects.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.ProjectAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.project.Project
import com.moban.model.response.project.ListProject
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_favorite_project.*
import kotlinx.android.synthetic.main.nav.view.*

class FavoriteProjectActivity : BaseMvpActivity<IFavoriteProjectView, IFavoriteProjectPresenter>(), IFavoriteProjectView {
    override var presenter: IFavoriteProjectPresenter = FavoriteProjectPresenterIml()
    private var page: Int = 1
    private var projectAdapter: ProjectAdapter = ProjectAdapter()
    var numFavoriteProject: Int = 0

    companion object {
        fun start(context: Context) {
            val bundle = Bundle()

            val intent = Intent(context, FavoriteProjectActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_favorite_project
    }

    override fun initialize(savedInstanceState: Bundle?) {
        favorite_project_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        favorite_project_nav.nav_tvTitle.text = getString(R.string.more_favorite_project)

        favorite_project_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        favorite_project_refresh_layout.setOnRefreshListener {
            reloadFavoriteProject()
        }

        initRecyclerView()

        presenter.loadFavoriteProjects(page)
        setGAScreenName("My Favorite Project", GACategory.PROJECT)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        favorite_project_recycler_view.layoutManager = linearLayoutManager
        favorite_project_recycler_view.isNestedScrollingEnabled = false

        projectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                ProjectDetailActivity.startForResult(this@FavoriteProjectActivity, project)
            }

            override fun onFavorite(project: Project) {
                presenter.unFavoriteProject(project)
            }
        }

        projectAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadFavoriteProjects(page)
            }
        }

        favorite_project_recycler_view.adapter = projectAdapter
    }

    private fun reloadFavoriteProject() {
        page = 1
        presenter.loadFavoriteProjects(page)
    }

    override fun bindingProject(listProject: ListProject, canLoadMore: Boolean) {
        favorite_project_refresh_layout.isRefreshing = false

        listProject.paging?.let {
            numFavoriteProject = it.total_items
            updateNumFavoriteProject()
        }

        if (projectAdapter.projects.isEmpty() || page == 1) {
            projectAdapter.setProjects(listProject.list, canLoadMore)
        }else {
            projectAdapter.appendProjects(listProject.list, canLoadMore)
        }

        if (canLoadMore) {
            page += 1
        }
    }

    override fun unFavoriteProject(project: Project) {
        projectAdapter.removeProject(project)
        LocalStorage.user()?.let {
            val numFavoriteProject = it.favorite_products_count - 1

            LocalStorage.updateNumFavoriteProduct(numFavoriteProject)
            val result = numFavoriteProject.toString() + " " + getString(R.string.tab_project)
            favorite_project_tv_result.text = result
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PROJECT_DETAIL_REQUEST) {
            val project = data?.getSerializableExtra(ProjectDetailActivity.BUNDLE_KEY_PROJECT) as Project?
            project?.let {
                if (!project.isFavorite) {
                    projectAdapter.removeProject(project)
                    numFavoriteProject--
                    updateNumFavoriteProject()
                }
            }
        }
    }

    private fun updateNumFavoriteProject() {
        val result = numFavoriteProject.toString() + " " + getString(R.string.tab_project)
        favorite_project_tv_result.text = result
        LocalStorage.updateNumFavoriteProduct(numFavoriteProject)
    }
}
