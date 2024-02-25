package com.moban.model.response.secondary.project

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.secondary.SecondaryProject
import com.moban.model.data.statistic.Statistic

/**
 * Created by LenVo on 3/11/19.
 */
class ListSecondaryProject : BaseModel() {
    var list: List<SecondaryProject> = ArrayList()
    var paging: Paging? = null
    var statistics: List<Statistic> = ArrayList()
}
