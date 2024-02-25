package com.moban.flow.reward

import com.moban.model.data.user.Level
import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpView

interface IRewardView: BaseMvpView {
    fun fillLevel(level: Level)
    fun fillGifts(gifts: List<LevelGift>)
    fun fillNextGifts(gifts: List<LevelGift>)
}
