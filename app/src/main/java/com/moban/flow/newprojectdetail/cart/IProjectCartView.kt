package com.moban.flow.newprojectdetail.cart

import com.moban.model.data.project.BlockData
import com.moban.mvp.BaseMvpView


/**
 * Created by H. Len Vo on 6/30/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectCartView: BaseMvpView {
    fun bindingApartments(blockData: BlockData, blockId: Int)
}
