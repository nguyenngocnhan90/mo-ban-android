package com.moban.flow.missions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.adapter.user.MissionAdapter
import com.moban.enum.GACategory
import com.moban.flow.missions.detail.MissionDetailActivity
import com.moban.handler.MissionItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.user.Mission
import com.moban.model.response.user.ListMissions
import com.moban.mvp.BaseMvpActivity
import com.moban.util.ViewUtil
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.activity_missions.*
import kotlinx.android.synthetic.main.nav.view.*

class MissionsActivity : BaseMvpActivity<IMissionsView, IMissionsPresenter>(), IMissionsView {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MissionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override var presenter: IMissionsPresenter = MissionsPresenterIml()

    private val inProgressAdapter = MissionAdapter()
    private val validAdapter = MissionAdapter()
    private val doneAdapter = MissionAdapter()
    private val expiredAdapter = MissionAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_missions
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("My Mission", GACategory.ACCOUNT)
        mission_nav.nav_tvTitle.text = getString(R.string.mission)
        mission_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        ViewUtil.setMarginTop(mission_tbToolbar, ViewUtil.getStatusBarHeight(this))

        // Init
        initRecyclerView()

        // Fill info
        fillUserInfo()

        // Load data
        presenter.getMissions()
    }

    private fun initRecyclerView() {
        val adapters = arrayOf(inProgressAdapter, validAdapter,
                doneAdapter, expiredAdapter)

        arrayOf(missions_recycler_view_in_progress,
                missions_recycler_view_valid,
                missions_recycler_view_done,
                missions_recycler_view_expired)
                .forEachIndexed { index, recyclerView ->
                    recyclerView.layoutManager = customLinearLayoutManager()
                    recyclerView.adapter = adapters[index]
                    adapters[index].listener = object : MissionItemHandler {
                        override fun onClicked(mission: Mission) {
                            MissionDetailActivity.start(this@MissionsActivity, mission)
                        }
                    }
                }
    }

    private fun customLinearLayoutManager(): CustomLinearLayoutManager {
        val layoutManager = CustomLinearLayoutManager(this)
        layoutManager.isScrollEnabled = false

        return layoutManager
    }

    private fun fillUserInfo() {
        LocalStorage.user()?.let {
            val currentCoin = it.currentCoinString() + " " + getString(R.string.lhc)
            missions_tv_point_badge.text = currentCoin
        }
    }

    /**
     * View implementation
     */

    override fun showMissions(missions: ListMissions) {
        missions.count?.let {
            missions_tv_done_count.text = it.completed.toString()
            missions_tv_in_progress_count.text = it.inprocess.toString()
        }

        val dataList = arrayOf(missions.inprocess, missions.effective, missions.completed, missions.expired)
        val adapters = arrayOf(inProgressAdapter, validAdapter, doneAdapter, expiredAdapter)
        val sectionViews = arrayOf(missions_view_in_progress, missions_view_valid,
                missions_view_done, missions_view_expired)

        adapters.forEachIndexed { index, adapter ->
            val list = dataList[index]
            adapter.missions = list
            sectionViews[index].visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
