package com.moban.flow.projects.searchproject

import com.moban.model.response.project.ListProject
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 7/22/18.
 */
interface ISearchProjectView: BaseMvpView {
    fun bindingProjects(listProject: ListProject, canLoadMore: Boolean)
    fun bindingProjectsWithKeyword(listProject: ListProject, canLoadMore: Boolean)
}
