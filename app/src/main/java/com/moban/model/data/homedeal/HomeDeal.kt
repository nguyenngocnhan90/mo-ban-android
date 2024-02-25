package com.moban.model.data.homedeal

import com.google.gson.annotations.SerializedName
import com.moban.enum.HomeDealType
import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.document.Document
import com.moban.model.data.project.Project
import com.moban.util.DateUtil

/**
 * Created by lenvo on 5/25/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class HomeDeal : BaseModel() {
    var type: String = ""
    var title: String = ""

    var projects: List<Project> = ArrayList()
    var banners: List<Document> = ArrayList()
    var deals: List<Document> = ArrayList()

    @SerializedName("listDeals")
    var listDeals: List<Document> = ArrayList()

    @SerializedName("dealPaging")
    var dealPaging: Paging? = null

    @SerializedName("endTime")
    var endTime: Double = 0.0

    fun isTimeout(): Boolean {
        return DateUtil.currentTimeSeconds() >= endTime
    }

    fun isValid(): Boolean {
        return HomeDealType.from(type) != HomeDealType.flashsale || !isTimeout()
    }
}
