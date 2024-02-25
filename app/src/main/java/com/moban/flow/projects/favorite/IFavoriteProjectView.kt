package com.moban.flow.projects.favorite

import com.moban.model.data.project.Project
import com.moban.model.response.project.ListProject
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 5/15/18.
 */
interface IFavoriteProjectView: BaseMvpView {
    fun bindingProject(listProject: ListProject, canLoadMore: Boolean)
    fun unFavoriteProject(project: Project)
}
