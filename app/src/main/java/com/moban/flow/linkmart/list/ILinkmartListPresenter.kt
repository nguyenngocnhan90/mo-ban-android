package com.moban.flow.linkmart.list

import com.moban.mvp.BaseMvpPresenter

interface ILinkmartListPresenter: BaseMvpPresenter<ILinkmartListView> {
    fun loadSpecialDeal(page: Int, cateId: Int? = null)
    fun loadNewestDeal(page: Int, cateId: Int? = null)
    fun loadPopularDeal(page: Int, cateId: Int? = null)
    fun getCategory(cateId: Int)
}
