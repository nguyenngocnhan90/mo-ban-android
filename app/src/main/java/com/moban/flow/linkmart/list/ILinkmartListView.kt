package com.moban.flow.linkmart.list

import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.mvp.BaseMvpView

interface ILinkmartListView: BaseMvpView {
    fun bindingAllProducts(products: List<Linkmart>)
    fun bindingCategory(cate: LinkmartCategory)
}
