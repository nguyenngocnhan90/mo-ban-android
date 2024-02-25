package com.moban.flow.reward.partner

import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpView

interface IRewardPartnerView: BaseMvpView {
    fun purchaseFailed(error: String? = null)
    fun markUseCompleted(gifts: List<LevelGift>, gift: LevelGift)
    fun purchaseCompleted()
}
