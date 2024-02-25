package com.moban.handler

import com.moban.model.data.user.LinkPointTransaction

/**
 * Created by LenVo on 5/22/18.
 */
interface LinkPointTransactionItemHandler {
    fun onClicked(linkPointTransaction: LinkPointTransaction)
}
