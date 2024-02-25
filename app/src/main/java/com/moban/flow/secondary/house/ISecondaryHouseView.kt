package com.moban.flow.secondary.house

import com.moban.model.response.secondary.ListSecondaryHouse
import com.moban.mvp.BaseMvpView

interface ISecondaryHouseView : BaseMvpView {
    fun bindingSecondaryProject(response: ListSecondaryHouse, canLoadMore: Boolean, total: Int)
    fun bindingLoadSecondaryFailed()
}
