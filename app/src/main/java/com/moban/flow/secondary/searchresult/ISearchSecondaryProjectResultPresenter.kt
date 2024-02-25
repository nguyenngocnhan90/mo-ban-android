package com.moban.flow.secondary.searchresult

import com.moban.model.param.SearchAdvanceParams
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 5/7/18.
 */
interface ISearchSecondaryProjectResultPresenter : BaseMvpPresenter<ISearchSecondaryProjectResultView> {
    fun searchProjectWithKeyword(page: Int, keyword: String)
    fun searchAdvance(searchAdvanceParams: SearchAdvanceParams)
}
