package com.moban.flow.secondary.search

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 7/22/18.
 */
interface ISearchSecondaryProjectPresenter : BaseMvpPresenter<ISearchSecondaryProjectView> {
    fun loadHighlightProject()
    fun searchProject(page: Int, keyword: String)
}
