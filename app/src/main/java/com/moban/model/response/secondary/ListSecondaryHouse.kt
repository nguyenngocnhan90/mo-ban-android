package com.moban.model.response.secondary

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.model.data.statistic.Statistic

/**
 * Created by LenVo on 3/11/19.
 */
class ListSecondaryHouse : BaseModel() {
    var list: List<SecondaryHouse> = ArrayList()
    var paging: Paging? = null
    var statistics: List<Statistic> = ArrayList()
}
