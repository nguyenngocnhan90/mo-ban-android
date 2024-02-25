package com.moban.flow.newprojectdetail.cart

import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 6/30/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectCartPresenter: BaseMvpPresenter<IProjectCartView> {
    fun loadApartments(id: Int, blockId: Int)
}
