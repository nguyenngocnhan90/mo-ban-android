package com.moban.model.response.chart

import com.moban.model.BaseModel
import com.moban.model.data.chart.ChartData

/**
 * Created by LenVo on 7/5/18.
 */
class ChartResponse: BaseModel() {
    var success: Boolean = false
    var data: List<ChartData> = ArrayList()
}
