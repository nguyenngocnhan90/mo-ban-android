package com.moban.model.param

import java.io.Serializable

/**
 * Created by LenVo on 7/25/18.
 */
class SearchAdvanceParams: Serializable{
    var types: List<Int>? = null
    var host: String? = null

    var minPrice: Long = 0
    var maxPrice: Long = 0

    var minRate: Int = 0
    var maxRate: Int = 0

    var districtId: Int? = null
    var cityId: Int? = null
}
