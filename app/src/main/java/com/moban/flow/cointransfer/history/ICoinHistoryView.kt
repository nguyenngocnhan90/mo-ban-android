package com.moban.flow.cointransfer.history

import com.moban.model.response.coin.ListLinkPointTransactions
import com.moban.model.response.coin.ListTransactions
import com.moban.mvp.BaseMvpView

interface ICoinHistoryView: BaseMvpView {
    fun bindingTransactions(transactions: ListTransactions)
    fun bindingLinkPointTransactions(transactions: ListLinkPointTransactions)
}
