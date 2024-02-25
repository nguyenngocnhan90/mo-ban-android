package com.moban.flow.projects

import com.moban.enum.ProjectHighlightType
import com.moban.enum.SortType
import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectsFragmentPresenter: BaseMvpPresenter<IProjectsFragmentView> {
    fun loadProjects(page: Int, sortType: SortType, filter: Int?, projectType: ProjectHighlightType = ProjectHighlightType.NONE)
}
