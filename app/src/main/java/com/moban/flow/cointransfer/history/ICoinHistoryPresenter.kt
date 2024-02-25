package com.moban.flow.cointransfer.history

import com.moban.mvp.BaseMvpPresenter

interface ICoinHistoryPresenter: BaseMvpPresenter<ICoinHistoryView> {
    fun loadTransactions(page: Int)
    fun loadLinkPointTransactions(page: Int)
}
