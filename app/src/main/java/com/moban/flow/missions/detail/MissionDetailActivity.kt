package com.moban.flow.missions.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moban.R
import com.moban.enum.GACategory
import com.moban.model.data.user.Mission
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DateUtil
import com.moban.util.DateUtil.Companion.DF_FULL_DATE_STRING
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.activity_mission_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class MissionDetailActivity : BaseMvpActivity<IMissionDetailView, IMissionDetailPresenter>(), IMissionDetailView {
    override var presenter: IMissionDetailPresenter = MissionDetailPresenterIml()

    companion object {
        const val BUNDLE_KEY_MISSION = "BUNDLE_KEY_MISSION"
        const val BUNDLE_KEY_MISSION_ID = "BUNDLE_KEY_MISSION_ID"

        fun start(context: Context, mission: Mission) {
            val intent = Intent(context, MissionDetailActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_MISSION, mission)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }

        fun start(context: Context, id: Int) {
            val intent = Intent(context, MissionDetailActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_MISSION_ID, id)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mission_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        mission_detail_nav.nav_tvTitle.text = getString(R.string.mission)
        mission_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        var idMission = 0
        if (intent.hasExtra(BUNDLE_KEY_MISSION)) {
            val mission = bundle?.getSerializable(BUNDLE_KEY_MISSION) as Mission

            LHPicasso.loadImage(mission.badge_Image, mission_detail_img)
            mission_detail_tv_type.text = mission.mission_Type_Name
            mission_detail_tv_name.text = mission.mission_Name
            LHPicasso.loadImage(mission.badge_Image, mission_detail_img_badge_receive)

            val coinsReceive = mission.linkCoin.toString() + " " + getString(R.string.lhc)
            mission_detail_tv_coins_receive_val.text = coinsReceive

            presenter.loadMissionDetail(mission.id)
            idMission = mission.id
        } else if (intent.hasExtra(BUNDLE_KEY_MISSION_ID)) {
            val id = bundle?.getSerializable(BUNDLE_KEY_MISSION_ID) as Int
            presenter.loadMissionDetail(id)
            idMission = id
        }

        setGAScreenName("Mission Detail $idMission", GACategory.ACCOUNT)
    }

    override fun bindingMissionDetail(mission: Mission) {
        mission_detail_tv_description_sub.text = mission.mission_Content
        LHPicasso.loadImage(mission.badge_Image, mission_detail_img)
        mission_detail_tv_type.text = mission.mission_Type_Name
        mission_detail_tv_name.text = mission.mission_Name
        LHPicasso.loadImage(mission.badge_Image, mission_detail_img_badge_receive)

        val coinsReceive = mission.linkCoin.toString() + " " + getString(R.string.lhc)
        mission_detail_tv_coins_receive_val.text = coinsReceive

        var time = getString(R.string.unknown)
        if (mission.start_Date > 0 && mission.end_Date > 0) {
            time = DateUtil.dateStringFromSeconds(mission.start_Date.toLong(), DF_FULL_DATE_STRING) +
                    " - " + DateUtil.dateStringFromSeconds(mission.end_Date.toLong(), DF_FULL_DATE_STRING)
        }
        mission_detail_tv_time.text = time
    }
}
