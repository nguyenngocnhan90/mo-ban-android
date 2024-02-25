package com.moban.flow.cointransfer.detail

import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 3/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface ICoinTransactionPresenter: BaseMvpPresenter<ICoinTransactionView> {
    fun getDetail(id: Int)
}
