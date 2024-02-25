package com.moban.flow.linkmart.detail

import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpView

interface ILinkmartDetailView: BaseMvpView {
    fun bindingLevelGift(levelGift: LevelGift)
    fun loadLevelGiftFailed()
    fun bindingProductDetail(linkmart: Linkmart)
    fun bindingProductPurchased(linkmartOrder: LinkmartOrder)
    fun bindingPurchaseFailed(error: String?)
    fun purchaseFailed(error: String? = null)
    fun markUseCompleted(gifts: List<LevelGift>, gift: LevelGift)
    fun purchaseCompleted()
}
