package com.moban.model.response

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.statistic.Statistic


class BaseDataList <T: BaseModel>: BaseModel() {
    var list: List<T> = ArrayList()
    var paging: Paging? = null
    var statistics: List<Statistic> = ArrayList()
}
