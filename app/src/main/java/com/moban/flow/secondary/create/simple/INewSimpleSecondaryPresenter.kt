package com.moban.flow.secondary.create.simple

import com.moban.model.param.NewSecondaryProjectParam
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by lenvo on 2020-02-25.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface INewSimpleSecondaryPresenter: BaseMvpPresenter<INewSimpleSecondaryView> {
    fun getBaseProjects()
    fun submit(param: NewSecondaryProjectParam)
}
