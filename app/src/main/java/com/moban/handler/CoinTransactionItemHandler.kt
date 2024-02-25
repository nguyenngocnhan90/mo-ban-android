package com.moban.handler

import com.moban.model.data.user.LhcTransaction

/**
 * Created by LenVo on 5/22/18.
 */
interface CoinTransactionItemHandler {
    fun onClicked(lhcTransaction: LhcTransaction)
}
