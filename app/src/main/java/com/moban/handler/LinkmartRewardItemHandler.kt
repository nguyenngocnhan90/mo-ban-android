package com.moban.handler

import com.moban.model.data.user.LevelGift

/**
 * Created by LenVo on 9/8/18.
 */
interface LinkmartRewardItemHandler {
    fun onClicked(gift: LevelGift)
    fun onReceivedGift(gift: LevelGift)
}
