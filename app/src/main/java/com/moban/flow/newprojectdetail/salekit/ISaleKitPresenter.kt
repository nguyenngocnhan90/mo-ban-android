package com.moban.flow.newprojectdetail.salekit

import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 6/28/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface ISaleKitPresenter: BaseMvpPresenter<ISaleKitView> {
    fun loadSaleKits(id: Int)
}
