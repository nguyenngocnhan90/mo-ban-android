package com.moban.model.response.secondary.constant

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.secondary.constant.HouseType
import com.moban.model.data.statistic.Statistic

/**
 * Created by LenVo on 3/11/19.
 */
class ListSecondaryHouseType : BaseModel() {
    var list: List<HouseType> = ArrayList()
    var paging: Paging? = null
    var statistics: List<Statistic> = ArrayList()
}
