package com.moban.flow.linkmart

import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpPresenter

interface ILinkmartPresenter: BaseMvpPresenter<ILinkmartView> {
    fun loadCategories(parentId: Int, perPage: Int?)
    fun loadSpecialDeal(cateId: Int)
    fun loadNewestDeal(cateId: Int)
    fun loadPopularDeal(cateId: Int)
    fun loadPurchased(page: Int)
    fun loadReward(page: Int)
    fun loadProfile()
    fun markUseGift(gift: LevelGift)
    fun buyLinkmart(gift: LevelGift)
}
