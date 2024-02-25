package com.moban.flow.homedeal

import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 6/13/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IHomeDealPresenter : BaseMvpPresenter<IHomeDealView> {
    fun get(typeId: String)
    fun getListDeals(typeId: String, page: Int)
}
