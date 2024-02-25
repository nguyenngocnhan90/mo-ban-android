package com.moban.flow.secondary.house

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.project.ProjectTypeAdapter
import com.moban.adapter.secondary.SecondaryHouseAdapter
import com.moban.enum.GACategory
import com.moban.flow.secondary.create.simple.NewSimpleSecondaryActivity
import com.moban.flow.secondary.detail.SecondaryProjectDetailActivity
import com.moban.flow.secondary.search.SearchSecondaryProjectActivity
import com.moban.handler.ItemSecondaryMenuBottomHandler
import com.moban.handler.LoadMoreHandler
import com.moban.handler.SecondaryHouseItemHandler
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.model.data.secondary.SecondaryProject
import com.moban.model.response.secondary.ListSecondaryHouse
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogBottomSheetUtil
import kotlinx.android.synthetic.main.activity_secondary_houses.*
import kotlinx.android.synthetic.main.nav.view.*

class SecondaryHouseActivity : BaseMvpActivity<ISecondaryHouseView, ISecondaryHousePresenter>(), ISecondaryHouseView {
    override var presenter: ISecondaryHousePresenter = SecondaryHousePresenterIml()
    private val secondaryHouseAdapter: SecondaryHouseAdapter = SecondaryHouseAdapter()
    private var page: Int = 1
    private lateinit var project: SecondaryProject
    private var projectTypeAdapter: ProjectTypeAdapter = ProjectTypeAdapter()

    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        fun start(context: Context, project: SecondaryProject) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)

            val intent = Intent(context, SecondaryHouseActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_secondary_houses
    }

    override fun initialize(savedInstanceState: Bundle?) {
        secondary_project_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        secondary_project_nav.nav_tvSearchProject.visibility = View.VISIBLE
        secondary_project_nav.nav_tvSearchProject.text = getString(R.string.search_secondary)
        secondary_project_nav.nav_tvSearchProject.setOnClickListener {
            SearchSecondaryProjectActivity.start(this@SecondaryHouseActivity)
        }

        secondary_project_nav.nav_add.visibility = View.VISIBLE
        secondary_project_nav.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogBottomSheetUtil.showDialogListActionSecondary(this, object: ItemSecondaryMenuBottomHandler {
                override fun onAddNewProject() {
                    dialog?.dismiss()
                    NewSimpleSecondaryActivity.start(this@SecondaryHouseActivity)
                }
            })
        }

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as SecondaryProject
        }

        initRecycleView()
        loadSecondaryConstants()
        setGAScreenName("List Secondary", GACategory.SECONDARY)
    }

    private fun loadSecondaryConstants() {
        if (!LHApplication.instance.lhCache.isLoadedSecondaryConstant) {
            presenter.loadBasicContants()
            presenter.getHouseTypes()
            presenter.getTargetHouseTypes()
            presenter.getPriceUnits()
            presenter.getAgentPriceUnits()
            presenter.getDirections()
            presenter.getBaseProjects()
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        secondary_project_recycler_view.layoutManager = layoutManager
        secondary_project_recycler_view.adapter = secondaryHouseAdapter
        secondaryHouseAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadSecondaryProject(project.id, page)
            }
        }

        secondaryHouseAdapter.listener = object: SecondaryHouseItemHandler {
            override fun onClicked(house: SecondaryHouse) {
                SecondaryProjectDetailActivity.start(this@SecondaryHouseActivity, house)
            }
        }

        val layoutManagerProjectType = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        secondary_project_recycler_view_type.layoutManager = layoutManagerProjectType
        secondary_project_recycler_view_type.adapter = projectTypeAdapter

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
            secondaryHouseAdapter.clearSecondaryProjects()
        }
        presenter.loadSecondaryProject(project.id, page)
    }

    override fun bindingLoadSecondaryFailed() {
        secondary_project_refresh_layout.isRefreshing = false
        if (page == 1) {
            val totalProject = getString(R.string.has_num_secondary_projects).replace("\$N", "0")
            secondary_project_tv_num_projects.text = totalProject
        }
    }

    override fun bindingSecondaryProject(response: ListSecondaryHouse, canLoadMore: Boolean, total: Int) {
        if (page == 1) {
            secondaryHouseAdapter.setListSecondaryProject(response.list, canLoadMore)
        } else {
            secondaryHouseAdapter.appendSecondaryProject(response.list, canLoadMore)
        }

        val statistics = response.statistics
        if (!statistics.isEmpty()) {
            projectTypeAdapter.setStatisticsList(statistics)
        }

        page += if (canLoadMore) 1 else 0
        val totalProject = getString(R.string.has_num_secondary_projects).replace("\$N", total.toString())
        secondary_project_tv_num_projects.text = totalProject
        secondary_project_refresh_layout.isRefreshing = false
    }
}
