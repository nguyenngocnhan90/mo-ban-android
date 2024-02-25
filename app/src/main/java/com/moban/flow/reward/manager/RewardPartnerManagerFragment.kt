package com.moban.flow.reward.manager

import android.os.Bundle
import com.moban.R
import com.moban.extension.toFullPriceString
import com.moban.flow.reward.RewardActivity
import com.moban.helper.LocalStorage
import com.moban.model.data.user.Level
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpFragment
import kotlinx.android.synthetic.main.fragment_reward_manager.*

class RewardPartnerManagerFragment: BaseMvpFragment<IRewardPartnerManagerView, IRewardPartnerManagerPresenter>(), IRewardPartnerManagerView {
    override var presenter: IRewardPartnerManagerPresenter = RewardPartnerManagerPresenterIml()
    private lateinit var user: User
    private var level: Level? = null
    private var titleName = "Bạn"

    companion object {
        fun createFragment(user: User): RewardPartnerManagerFragment {
            val fragment = RewardPartnerManagerFragment()
            fragment.user = user
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_reward_manager
    }


    override fun initialize(savedInstanceState: Bundle?) {
        if (!::user.isInitialized) {
            val rewardActivity = activity as RewardActivity
            user = rewardActivity.getUser()
        }

        level?.let {
            fillLevel(it)
        }

        LocalStorage.user()?.let { it ->
            if(user.id != it.id) {
                titleName = user.name
            }
        }

        val title = getString(R.string.reward_level_reach_title).replace("\$N", titleName)
        reward_manager_tv_title_level.text = title
        reward_manager_tv_level.text = user.current_Level_QLDT_Name
        val currentKpi = "KPIS ${user.current_Level_QLDT_Name}"
        reward_manager_tv_current_kpi.text = currentKpi

        reward_manager_tv_next_level_name.text = user.next_Level_QLDT_Name
        val nextKpi = "KPIS ${user.next_Level_QLDT_Name}"
        reward_manager_tv_next_kpi.text = nextKpi
    }

    fun fillLevel(level: Level) {
        this.level = level
        if (!isAdded) {
            return
        }
        val groupRevenue = ">= ${level.revenue.toFullPriceString()} VNĐ"
        reward_level_tv_group_revenue.text = groupRevenue

        val linkPoint = ">= ${level.linkPoint.toFullPriceString()}"
        reward_level_tv_link_point.text = linkPoint

        val numPartner = ">= ${level.management_User_Count.toFullPriceString()}"
        reward_level_tv_num_partner.text = numPartner

        val review = "${level.user_Rating}*"
        reward_level_tv_review_partner.text = review

        val supportFee = "${level.salary.toFullPriceString()} VNĐ"
        reward_level_tv_support_fee.text = supportFee

        val managementFee = "${level.bonus_Fee}%"
        reward_level_tv_management_fee.text = managementFee

        level.next_Level?.let {
            fillNextLevel(it)
        }
    }

    private fun fillNextLevel(level: Level) {
        val groupRevenue = ">= ${level.revenue.toFullPriceString()} VNĐ"
        reward_next_level_tv_group_revenue.text = groupRevenue

        val linkPoint = ">= ${level.linkPoint.toFullPriceString()}"
        reward_next_level_tv_link_point.text = linkPoint

        val numPartner = ">= ${level.management_User_Count.toFullPriceString()}"
        reward_next_level_tv_num_partner.text = numPartner

        val review = "${level.user_Rating}*"
        reward_next_level_tv_review_partner.text = review

        val supportFee = "${level.salary.toFullPriceString()} VNĐ"
        reward_next_level_tv_support_fee.text = supportFee

        val managementFee = "${level.bonus_Fee}%"
        reward_next_level_tv_management_fee.text = managementFee
    }
}
