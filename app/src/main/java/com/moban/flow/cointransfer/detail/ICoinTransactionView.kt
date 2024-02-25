package com.moban.flow.cointransfer.detail

import com.moban.model.data.user.LhcTransaction
import com.moban.mvp.BaseMvpView


/**
 * Created by H. Len Vo on 3/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface ICoinTransactionView: BaseMvpView {
    fun bindingTransaction(transaction: LhcTransaction)
}
