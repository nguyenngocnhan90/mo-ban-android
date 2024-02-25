package com.moban.flow.secondary.project

import com.moban.model.response.secondary.project.ListSecondaryProject
import com.moban.mvp.BaseMvpView

interface ISecondaryProjectView : BaseMvpView {
    fun bindingSecondaryProject(response: ListSecondaryProject, canLoadMore: Boolean, total: Int)
    fun bindingLoadSecondaryFailed()
}
