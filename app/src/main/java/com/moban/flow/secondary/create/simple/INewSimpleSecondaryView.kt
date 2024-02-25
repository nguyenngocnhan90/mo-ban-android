package com.moban.flow.secondary.create.simple

import com.moban.mvp.BaseMvpView

/**
 * Created by lenvo on 2020-02-25.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface INewSimpleSecondaryView: BaseMvpView {
    fun showCreateSecondaryFailed(message: String?)
    fun showCreateSecondarySuccess(message: String?)
}
