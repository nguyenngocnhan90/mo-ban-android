package com.moban.handler

import com.moban.model.data.gift.Gift

/**
 * Created by LenVo on 4/11/18.
 */
interface GiftItemHandler {
    fun onClicked(deal: Gift)
}
