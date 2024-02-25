package com.moban.flow.newprojectdetail.subscribe

import com.moban.model.data.project.SalePackage
import com.moban.mvp.BaseMvpView


/**
 * Created by H. Len Vo on 7/1/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectSubscribeView: BaseMvpView {
    fun bindingPackages(packages: List<SalePackage>)
}
