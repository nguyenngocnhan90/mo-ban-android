package com.moban.handler

import com.moban.model.data.deal.Deal

/**
 * Created by LenVo on 3/30/18.
 */
interface DealItemHandler {
    fun onClicked(deal: Deal)
    fun onLongClicked(deal: Deal)
    fun onMainAction(deal: Deal)
    fun onTimeOut()
}
