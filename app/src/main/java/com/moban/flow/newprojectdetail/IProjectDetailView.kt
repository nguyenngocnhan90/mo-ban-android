package com.moban.flow.newprojectdetail

import com.moban.model.data.document.Document
import com.moban.model.data.document.DocumentGroup
import com.moban.model.data.project.Project
import com.moban.model.data.project.ProjectActivity
import com.moban.mvp.BaseMvpView

/**
 * Created by lenvo on 6/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectDetailView : BaseMvpView {
    fun bindingProjectDetail(project: Project)
    fun bindingSaleKitsProject(documents: List<DocumentGroup>)
    fun bindingSpecialDeal(documents: List<Document>)
    fun bindingRelatedProducts(projects: List<Project>)
    fun bindingRecentActivites(activities: List<ProjectActivity>)
}
