package com.moban.flow.projects

import com.moban.model.data.Paging
import com.moban.model.data.project.Project
import com.moban.model.data.statistic.Statistic
import com.moban.mvp.BaseMvpView

/**
 * Created by lenvo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectsFragmentView : BaseMvpView {
    fun bindingProject(projects: List<Project>, statistics: MutableList<Statistic>, paging: Paging?)
    fun bindingProjectFailed()
}
