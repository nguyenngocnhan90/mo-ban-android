package com.moban.flow.newprojectdetail.subscribe

import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 7/1/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectSubscribePresenter: BaseMvpPresenter<IProjectSubscribeView> {
    fun loadPackage(id: Int)
    fun joinPackage(id: Int, code: String)
}
