package com.moban.flow.linkmart.detail

import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpPresenter

interface ILinkmartPresenter: BaseMvpPresenter<ILinkmartDetailView> {
    fun loadLinkmartDetail(linkmartId: Int)
    fun getLevelGift(giftId: Int)
    fun purchase(linkmart: Linkmart)
    fun markUseGift(gift: LevelGift)
    fun buyGiftLinkmart(gift: LevelGift)
}
