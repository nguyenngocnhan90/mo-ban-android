package com.moban.flow.secondary.project

import com.moban.mvp.BaseMvpPresenter

interface ISecondaryProjectPresenter: BaseMvpPresenter<ISecondaryProjectView> {
    fun loadProjects(page: Int)
}
