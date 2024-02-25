package com.moban.flow.reward.badge

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.moban.R
import com.moban.adapter.badge.RewardBadgeAdapter
import com.moban.flow.badges.BadgeDetailActivity
import com.moban.flow.reward.RewardActivity
import com.moban.handler.BadgeItemHandler
import com.moban.model.data.user.Badge
import com.moban.model.data.user.User
import com.moban.model.response.badge.ListBadges
import com.moban.mvp.BaseMvpFragment
import kotlinx.android.synthetic.main.fragment_reward_badge.*

class RewardBadgeFragment: BaseMvpFragment<IRewardBadgeView, IRewardBadgePresenter>(), IRewardBadgeView {
    override var presenter: IRewardBadgePresenter = RewardBadgePresenterIml()
    private val badgeAdapter = RewardBadgeAdapter()
    private lateinit var user: User
    companion object {
        fun createFragment(user: User): RewardBadgeFragment {
            val fragment = RewardBadgeFragment()
            fragment.user = user
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_reward_badge
    }

    override fun initialize(savedInstanceState: Bundle?) {
        if (!::user.isInitialized) {
            val rewardActivity = activity as RewardActivity
            user = rewardActivity.getUser()
        }
        initRecycleView()
        presenter.loadBadges(user)
    }

    override fun bindingBadges(badges: ListBadges) {
        badgeAdapter.badges = badges.list
        badgeAdapter.notifyDataSetChanged()
    }

    private fun initRecycleView() {
        val layoutManager = GridLayoutManager(context, 3)
        reward_recycle_view_badge.layoutManager = layoutManager
        reward_recycle_view_badge.adapter = badgeAdapter

        badgeAdapter.listener = object : BadgeItemHandler {
            override fun onClicked(badge: Badge) {
                BadgeDetailActivity.start( this@RewardBadgeFragment.context, badge)
            }
        }
    }
}
