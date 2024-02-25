package com.moban.model.data.project

import com.moban.model.BaseModel
import com.moban.model.data.statistic.BlockStatistic

/**
 * Created by LenVo on 7/17/18.
 */
class BlockData: BaseModel() {
    var statistics: BlockStatistic = BlockStatistic()
    var list: List<Floor> = ArrayList()
}
