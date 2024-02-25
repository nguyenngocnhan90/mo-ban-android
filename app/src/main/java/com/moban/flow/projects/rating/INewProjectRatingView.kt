package com.moban.flow.projects.rating

import com.moban.model.data.project.ProjectRating
import com.moban.mvp.BaseMvpView

interface INewProjectRatingView: BaseMvpView {
    fun submittedRating(rating: ProjectRating)
    fun error(error: String)
    fun stopLoading()
}