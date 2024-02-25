package com.moban.flow.secondary.detail

import com.moban.mvp.BaseMvpPresenter

interface ISecondaryProjectDetailPresenter: BaseMvpPresenter<ISecondaryProjectDetailView> {
    fun loadProject(id: Int)
    fun requestContact(id: Int)
}
