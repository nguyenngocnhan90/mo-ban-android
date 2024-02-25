package com.moban.flow.projects

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.ProjectAdapter
import com.moban.adapter.project.ProjectTypeAdapter
import com.moban.enum.DealFilter
import com.moban.enum.ProjectHighlightType
import com.moban.enum.SortType
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.signin.SignInActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectItemHandler
import com.moban.handler.ProjectMenuHandler
import com.moban.handler.ProjectTypeItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.Paging
import com.moban.model.data.project.Project
import com.moban.model.data.statistic.Statistic
import com.moban.mvp.BaseMvpFragment
import com.moban.util.DialogBottomSheetUtil
import kotlinx.android.synthetic.main.fragment_projects.*


class ProjectsFragment : BaseMvpFragment<IProjectsFragmentView, IProjectsFragmentPresenter>(), IProjectsFragmentView {
    override var presenter: IProjectsFragmentPresenter = ProjectFragmentPresenterIml()

    override fun getLayoutId(): Int {
        return R.layout.fragment_projects
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initRecyclerView()
        refreshData()
        initBottomMenu()
    }

    var projectType = ProjectHighlightType.NONE
    private var page: Int = 1
    private var projectAdapter: ProjectAdapter = ProjectAdapter()
    private var projectTypeAdapter: ProjectTypeAdapter = ProjectTypeAdapter()

    private var sortType: SortType = SortType.HOT
    private lateinit var fragmentActivity: FragmentActivity
    private var sortDialog: Dialog? = null

    private var selectedStatistics: Statistic = Statistic()
    var allProjectCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        fragmentActivity = this.activity!!
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    private fun initBottomMenu() {
        project_tv_filter.text = sortType.getString(fragmentActivity)

        project_filter.setOnClickListener {
            sortDialog = DialogBottomSheetUtil.showDialogFeedOption(fragmentActivity, this.sortType,
                object : ProjectMenuHandler {
                    override fun onClickSort(sortType: SortType) {
                        this@ProjectsFragment.sortType = sortType
                        reloadProjectBySortType()
                    }
                })
        }
    }

    private fun reloadProjectBySortType() {
        project_tv_filter.text = sortType.getString(fragmentActivity)
        sortDialog?.dismiss()
        refreshData()
    }

    private fun initRecyclerView() {
        val context = context
        project_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        project_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        project_refresh_layout.isRefreshing = true

        val linearLayoutManager = LinearLayoutManager(context)
        project_recycler_view.layoutManager = linearLayoutManager

        projectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                LocalStorage.user()?.let {
                    if (it.isAnonymous() && project.is_Login_Required) {
                        activity?.let { activity ->
                            SignInActivity.start(context, activity)
                        }
                    }
                    else {
                        ProjectDetailActivity.start(context, project)
                    }
                }
            }

            override fun onFavorite(project: Project) {
            }
        }

        projectAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadProjects(page, sortType, getFilter(), projectType)
            }
        }

        project_recycler_view.adapter = projectAdapter


        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        project_recycler_view_type.layoutManager = layoutManager
        project_recycler_view_type.adapter = projectTypeAdapter
        projectTypeAdapter.listener = object: ProjectTypeItemHandler {
            override fun onSelected(type: Statistic) {
                projectTypeAdapter.selectStatistic(type)
                selectedStatistics = type
                page = 1
                project_refresh_layout?.isRefreshing = true
                presenter.loadProjects(page, sortType, getFilter(), projectType)
            }
        }
    }

    private fun getFilter(): Int? {
        return if (selectedStatistics.id > 0) selectedStatistics.id else null
    }

    private fun openMyDeals() {
        LocalStorage.user()?.let {
            ListReservationActivity.start(context, it, DealFilter.REVIEWABLE)
        }
    }

    private fun refreshData() {
        page = 1
        presenter.loadProjects(page, sortType, getFilter(), projectType)
    }

    override fun bindingProjectFailed() {
        project_refresh_layout?.isRefreshing = false
    }

    override fun bindingProject(projects: List<Project>, statistics: MutableList<Statistic>, paging: Paging?) {
        project_refresh_layout?.isRefreshing = false
        if (page == 1) {
            projectAdapter.projects.clear()
            if (selectedStatistics.id == 0) {
                projectTypeAdapter.statistics.clear()
            }
        }

        if (projects.isNotEmpty() && this@ProjectsFragment.isAdded) {
            paging?.let {
                if (selectedStatistics.id == 0) {
                    allProjectCount = paging.total_items
                }
                val totalProject = getString(R.string.has_num_projects).replace("\$N", paging.total_items.toString())
                project_tv_num_projects.text = totalProject

                val canLoadMore = page < paging.total

                page += 1

                if (projectAdapter.isDataEmpty) {
                    projectAdapter.setProjects(projects, canLoadMore)
                } else {
                    projectAdapter.appendProjects(projects, canLoadMore)
                }
            }
        }

        if (selectedStatistics.id == 0) {
            if (projectType == ProjectHighlightType.NONE) {
                var allType = statistics.firstOrNull { it.id == 0 }
                if (statistics.isEmpty() || allType == null) {
                    allType = Statistic()
                    allType.type = "Tất Cả"
                    allType.count = allProjectCount
                    statistics.add(0, allType)
                }
            }

            projectTypeAdapter.setStatisticsList(statistics)
            projectTypeAdapter.selectStatistic(selectedStatistics)
        }
    }
}
