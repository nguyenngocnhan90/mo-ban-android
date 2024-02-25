package com.moban.flow.secondary.search

import com.moban.model.response.secondary.ListSecondaryHouse
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 3/11/19.
 */
interface ISearchSecondaryProjectView: BaseMvpView {
    fun bindingHighlightProjects(listHouse: ListSecondaryHouse)
    fun bindingProjectsWithKeyword(listHouse: ListSecondaryHouse, canLoadMore: Boolean)
}
