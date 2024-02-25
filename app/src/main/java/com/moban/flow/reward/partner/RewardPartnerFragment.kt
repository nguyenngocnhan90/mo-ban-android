package com.moban.flow.reward.partner

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.moban.R
import com.moban.adapter.user.LevelGiftAdapter
import com.moban.flow.cointransfer.history.CoinHistoryActivity
import com.moban.flow.reward.RewardActivity
import com.moban.handler.LevelGiftHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.user.Level
import com.moban.model.data.user.LevelGift
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpFragment
import com.moban.util.BitmapUtil
import com.moban.util.Device
import com.moban.util.DialogUtil
import kotlinx.android.synthetic.main.fragment_reward_partner.*

class RewardPartnerFragment : BaseMvpFragment<IRewardPartnerView, IRewardPartnerPresenter>(), IRewardPartnerView {
    override var presenter: IRewardPartnerPresenter = RewardPartnerPresenterIml()
    private lateinit var user: User
    private var level: Level? = null
    private var gifts: List<LevelGift>? = null
    private var nextGifts: List<LevelGift>? = null
    private var titleName = "Bạn"

    private var giftsAdapter = LevelGiftAdapter()
    private var nextGiftsAdapter = LevelGiftAdapter()

    companion object {
        fun createFragment(user: User): RewardPartnerFragment {
            val fragment = RewardPartnerFragment()
            fragment.user = user
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_reward_partner
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val rewardActivity = activity as RewardActivity
        if (!::user.isInitialized) {
            user = rewardActivity.getUser()
        }
        LocalStorage.user()?.let { it ->
            if (user.id != it.id) {
                titleName = user.name
            } else {
                //Current User:
                reward_img_link_point.visibility = View.VISIBLE
                reward_view_link_point.setOnClickListener {
                    CoinHistoryActivity.start(rewardActivity, true)
                }
            }
        }
        initRecycleView()

        val title = getString(R.string.reward_level_reach_title).replace("\$N", titleName)
        reward_level_tv_title_level.text = title
        reward_level_tv_level.text = user.current_Level_DT_Name.toUpperCase()
        user.user_Coins?.let {
            reward_tv_level_point.text = it.levelPoint.toString()
            reward_tv_link_point.text = it.linkPoint.toString()
        }

        level?.let {
            fillLevel(it)
        }

        gifts?.let {
            fillGifts(it)
        }

        nextGifts?.let {
            fillNextGifts(it)
        }
    }

    fun fillGifts(gifts: List<LevelGift>) {
        this.gifts = gifts
        if (!isAdded) {
            return
        }
        giftsAdapter.setListGifts(gifts)
    }

    fun fillNextGifts(gifts: List<LevelGift>) {
        this.nextGifts = gifts
        if (!isAdded) {
            return
        }
        nextGiftsAdapter.setListGifts(gifts)
    }

    private fun initRecycleView() {
        val giftsLayoutManager = LinearLayoutManager(context)
        reward_recycle_view_available_gifts.isNestedScrollingEnabled = false
        reward_recycle_view_available_gifts.layoutManager = giftsLayoutManager
        reward_recycle_view_available_gifts.adapter = giftsAdapter
        LocalStorage.user()?.let {
            giftsAdapter.isShowStatus = user.id == it.id
        }
        giftsAdapter.listener = object : LevelGiftHandler {
            override fun onClicked(gift: LevelGift) {
                var dialog: Dialog? = null
                dialog = DialogUtil.showConfirmDialog(context, false,
                        getString(R.string.get_gift), getString(R.string.get_gift_confirm),
                        getString(R.string.ok), getString(R.string.skip),
                        View.OnClickListener {
                            dialog?.dismiss()
                            buyGift(gift)
                        }, View.OnClickListener {
                    dialog?.dismiss()
                })
            }
        }

        val nextGiftsLayoutManager = LinearLayoutManager(context)
        reward_recycle_view_next_available_gifts.isNestedScrollingEnabled = false
        reward_recycle_view_next_available_gifts.layoutManager = nextGiftsLayoutManager
        reward_recycle_view_next_available_gifts.adapter = nextGiftsAdapter
        nextGiftsAdapter.isShowStatus = false
    }

    private fun buyGift(gift: LevelGift) {
        if (gift.linkmart_Id > 0 && gift.gift_level_Id > 0) {
            val rewardActivity = activity as RewardActivity
            rewardActivity.showLoading()
            presenter.markUseGift(gift)
        } else {
            DialogUtil.showErrorDialog(context, getString(R.string.get_gift_not_found_failed_title),
                    getString(R.string.get_gift_failed_desc), getString(R.string.ok), null)
        }
    }

    fun fillLevel(level: Level) {
        this.level = level
        if (!isAdded) {
            return
        }
        fillMissingCoins(level)
        val agentFee = (if (level.bonus_Fee > 0) "+" else "") +  level.bonus_Fee.toString() + "%"
        reward_tv_agent_fee.text = agentFee
        val depositFee = level.deposit_Rate.toString() + "%"
        reward_tv_deposit_fee.text = depositFee

        level.next_Level?.let {
            reward_tv_next_level.text = it.level_Name

            val nextAgentFee = (if (it.bonus_Fee > 0) "+" else "") +
                    it.bonus_Fee.toString() + "%"
            reward_tv_next_agent_fee.text = nextAgentFee

            val nextDepositFee = it.deposit_Rate.toString() + "%"
            reward_tv_next_deposit_fee.text = nextDepositFee

            val nextLevel = getString(R.string.reward_next_level_point) + " " + it.level_Point.toString()
            reward_tv_point_next_level.text = nextLevel
        }

    }

    private fun fillMissingCoins(level: Level) {
        val currentPoint = if (user.user_Coins != null) user.user_Coins!!.levelPoint else 0
        val nextPoint = if (level.next_Level != null) level.next_Level!!.level_Point else 0
        val progressWidth = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(context, 80)) * currentPoint / nextPoint.toFloat()

        val remainProgressLayout = reward_remain_coin_progress.layoutParams
        remainProgressLayout.width = progressWidth.toInt()
        reward_remain_coin_progress.layoutParams = remainProgressLayout

        val reachLevel = getString(R.string.reward_level_point_reach)
                .replace("\$N", titleName)
                .replace("\$P", currentPoint.toString())
        reward_tv_reach_level.text = reachLevel

        val nextLevel = getString(R.string.reward_level_need_point_to_next_level)
                .replace("\$P", (nextPoint - currentPoint).toString())
        reward_tv_next_point_need_to_next_lv.text = nextLevel
    }

    override fun purchaseFailed(error: String?) {
        val rewardActivity = activity as RewardActivity
        rewardActivity.hideLoading()
        val message = error ?: getString(R.string.get_gift_failed_desc)
        DialogUtil.showErrorDialog(rewardActivity, getString(R.string.get_gift_failed_title),
                message, getString(R.string.ok), null)
    }

    override fun markUseCompleted(gifts: List<LevelGift>, gift: LevelGift) {
        if (gifts.isNotEmpty()) {
            fillGifts(gifts)
        }

        gift.using_status = 1
        giftsAdapter.updateGift(gift)

        presenter.buyLinkmart(gift)
    }

    override fun purchaseCompleted() {
        val rewardActivity = activity as RewardActivity
        rewardActivity.hideLoading()
        DialogUtil.showInfoDialog(context, getString(R.string.get_gift_congrats_title),
                getString(R.string.get_gift_congrats_desc), getString(R.string.ok), null)
    }
}
