package com.moban.flow.reward

import com.moban.mvp.BaseMvpPresenter

interface IRewardPresenter: BaseMvpPresenter<IRewardView> {
    fun getLevel(levelId: Int)
    fun getAvailableGifts(userId: Int)
    fun getLevelGifts(levelId: Int)
}
