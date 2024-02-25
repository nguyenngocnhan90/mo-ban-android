package com.moban.flow.secondary.searchresult

import com.moban.model.response.secondary.ListSecondaryHouse
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 5/7/18.
 */
interface ISearchSecondaryProjectResultView : BaseMvpView {
    fun bindingProject(listHouse: ListSecondaryHouse, canLoadMore: Boolean)
}
