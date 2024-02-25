package com.moban.handler

import com.moban.model.data.linkmart.LinkmartOrder

/**
 * Created by LenVo on 9/8/18.
 */
interface PurchasedLinkmartItemHandler {
    fun onClicked(linkmartOrder: LinkmartOrder)
}
