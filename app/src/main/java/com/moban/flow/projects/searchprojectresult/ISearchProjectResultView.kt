package com.moban.flow.projects.searchprojectresult

import com.moban.model.response.project.ListProject
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 5/7/18.
 */
interface ISearchProjectResultView : BaseMvpView {
    fun bindingProject(listProject: ListProject, canLoadMore: Boolean)
}
