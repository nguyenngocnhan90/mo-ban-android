package com.moban.flow.projects.favorite

import com.moban.model.data.project.Project
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 5/15/18.
 */
interface IFavoriteProjectPresenter: BaseMvpPresenter<IFavoriteProjectView> {
    fun loadFavoriteProjects(page: Int)
    fun unFavoriteProject(project: Project)
}
