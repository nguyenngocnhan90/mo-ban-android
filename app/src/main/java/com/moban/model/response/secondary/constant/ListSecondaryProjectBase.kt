package com.moban.model.response.secondary.constant

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.secondary.constant.ProjectBase
import com.moban.model.data.statistic.Statistic

class ListSecondaryProjectBase : BaseModel() {
    var list: List<ProjectBase> = ArrayList()
    var paging: Paging? = null
    var statistics: List<Statistic> = ArrayList()
}
