package com.moban.flow.projects.rating

import com.moban.model.param.project.ProjectRatingParam
import com.moban.mvp.BaseMvpPresenter

interface INewProjectRatingPresenter: BaseMvpPresenter<INewProjectRatingView> {
    fun submitRating(projectId: Int, zparam: ProjectRatingParam)
}