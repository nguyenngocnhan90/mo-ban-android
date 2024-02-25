package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.project.Project
import com.moban.model.data.statistic.Statistic

/**
 * Created by LenVo on 3/7/18.
 */
class ListProject : BaseModel() {
    var list: List<Project> = ArrayList()
    var paging: Paging? = null
    var statistics: List<Statistic> = ArrayList()
}
