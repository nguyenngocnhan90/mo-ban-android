package com.moban.model.response.deal

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.deal.Deal

/**
 * Created by LenVo on 3/30/18.
 */
class ListDeals : BaseModel() {
    @SerializedName("deals_Count")
    var deals_Count: Int = 0

    @SerializedName("deals_RevenueCount")
    var deals_RevenueCount: Int = 0

    @SerializedName("agent_Revenue")
    var agent_Revenue: Double = 0.0

    var list: List<Deal> = ArrayList()
    var paging: Paging? = null

    fun getAgentRevenueMillion(): String {
        var price = agent_Revenue.toFloat() / 1000000.0
        var string = String.format("%.1f", price)
        if (string.contains(",0") || string.contains(".0")) {
            string = string.replace(",0", "").replace(".0", "")
        }
        return string
    }
}
