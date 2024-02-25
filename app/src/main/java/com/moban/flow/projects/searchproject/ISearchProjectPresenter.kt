package com.moban.flow.projects.searchproject

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 7/22/18.
 */
interface ISearchProjectPresenter : BaseMvpPresenter<ISearchProjectView> {
    fun loadProject(page: Int)
    fun searchProject(page: Int, keyword: String)
}
