package com.moban.flow.reward

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.enum.Rank
import com.moban.flow.reward.badge.RewardBadgeFragment
import com.moban.flow.reward.manager.RewardPartnerManagerFragment
import com.moban.flow.reward.partner.RewardPartnerFragment
import com.moban.model.data.user.Level
import com.moban.model.data.user.LevelGift
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Font
import com.moban.util.NetworkUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_reward.*
import kotlinx.android.synthetic.main.nav.view.*

class RewardActivity : BaseMvpActivity<IRewardView, IRewardPresenter>(), IRewardView {
    override var presenter: IRewardPresenter = RewardPresenterIml()

    private lateinit var partnerFragment: RewardPartnerFragment
    private lateinit var managerFragment: RewardPartnerManagerFragment
    private lateinit var badgeFragment: RewardBadgeFragment

    private val fragmentManager = supportFragmentManager
    private lateinit var user: User
    private var partnerLevel: Int = 0
    private var partnerManagerLevel: Int = 0

    var isPartnerRole: Boolean = false
    var isPartnerManagerRole: Boolean = false


    companion object {
        const val BUNDLE_KEY_USER = "BUNDLE_KEY_USER"

        fun start(context: Context, user: User) {
            val intent = Intent(context, RewardActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USER, user)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_reward
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Rewards", GACategory.ACCOUNT)
        reward_tbToolbar.nav_tvTitle.text = getString(R.string.reward)
        reward_tbToolbar.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_USER)) {
            return
        }
        user = bundle?.getSerializable(BUNDLE_KEY_USER) as User
        checkRanks()

        partnerFragment = RewardPartnerFragment.createFragment(user)
        managerFragment = RewardPartnerManagerFragment.createFragment(user)
        badgeFragment = RewardBadgeFragment.createFragment(user)

        reward_view_level_manager.visibility = if (isPartnerManagerRole) View.VISIBLE else View.GONE
        reward_view_level.visibility = if (isPartnerRole) View.VISIBLE else View.GONE

        var indexPage = 2
        if (isPartnerManagerRole) {
            indexPage = 1
        }
        if (isPartnerRole) {
            indexPage = 0
        }
        showPageByIndex(indexPage)

        partnerLevel = user.current_Level_DT
        partnerManagerLevel = user.current_Level_QLDT

        getRewardDetail()

        arrayOf(reward_btn_level, reward_btn_level_manager, reward_btn_badge)
                .forEachIndexed { index, view ->
                    view?.setOnClickListener {
                        showPageByIndex(index)
                    }
                }
    }

    fun getUser(): User {
        return user
    }

    private fun getRewardDetail() {
        if (!NetworkUtil.hasConnection(this)) {
            var dialog: Dialog? = null
            dialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dialog?.dismiss()
                getRewardDetail()
            })
            return
        }

        if (isPartnerRole && partnerLevel > 0) {
            presenter.getLevel(partnerLevel)
            presenter.getLevelGifts(user.next_Level_DT)
            presenter.getAvailableGifts(user.id)
        }

        // For manager
        if (isPartnerManagerRole && partnerManagerLevel > 0) {
            presenter.getLevel(partnerManagerLevel)
        }
    }

    private fun showPageByIndex(selectIndex: Int) {
        arrayOf(reward_line_level, reward_line_level_manager, reward_line_badge)
                .forEachIndexed { index, view ->
                    view.visibility = if (index == selectIndex) View.VISIBLE else View.GONE
                }

        val fontNormal = Font.regularTypeface(this)
        val fontBold = Font.boldTypeface(this)
        arrayOf(reward_btn_level, reward_btn_level_manager, reward_btn_badge)
                .forEachIndexed { index, view ->
                    val isCurrentIndex = index == selectIndex

                    view.typeface = if (isCurrentIndex) fontBold else fontNormal
                    view.setTextColor(Util.getColor(this,
                            if (isCurrentIndex) R.color.color_black
                            else R.color.color_black_50))
                }

        val fragments = arrayOf(partnerFragment, managerFragment, badgeFragment)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.reward_frame_container, fragments[selectIndex])
        fragmentTransaction.commit()
    }

    private fun checkRanks() {
        if (!user.isBO) {
            if (user.getRank() == Rank.PARTNER) {
                isPartnerRole = true
            } else if (user.getRank() == Rank.PARTNER_MANAGER) {
                isPartnerRole = true
                isPartnerManagerRole = true
            }
        }
    }

    override fun fillLevel(level: Level) {
        if (level.id == partnerLevel) {
            partnerFragment.fillLevel(level)
        }

        if (level.id == partnerManagerLevel) {
            managerFragment.fillLevel(level)
        }
    }

    override fun fillGifts(gifts: List<LevelGift>) {
        partnerFragment.fillGifts(gifts)
    }

    override fun fillNextGifts(gifts: List<LevelGift>) {
        partnerFragment.fillNextGifts(gifts)
    }

    fun showLoading() {
        reward_progressbar.visibility = View.VISIBLE
    }

    fun hideLoading() {
        reward_progressbar.visibility = View.GONE
    }
}
