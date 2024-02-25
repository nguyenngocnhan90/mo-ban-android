package com.moban.flow.newprojectdetail.salekit

import com.moban.model.data.document.DocumentGroup
import com.moban.mvp.BaseMvpView


/**
 * Created by H. Len Vo on 6/28/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface ISaleKitView : BaseMvpView {
    fun bindingSaleKitsProject(documents: List<DocumentGroup>)
}
