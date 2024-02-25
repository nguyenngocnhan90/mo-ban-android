package com.moban.flow.projects.searchprojectresult

import com.moban.model.param.SearchAdvanceParams
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 5/7/18.
 */
interface ISearchProjectResultPresenter : BaseMvpPresenter<ISearchProjectResultView> {
    fun searchProjectWithKeyword(page: Int, keyword: String)
    fun searchAdvance(searchAdvanceParams: SearchAdvanceParams)
}
