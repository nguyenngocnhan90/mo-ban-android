package com.moban.model.data.homedeal

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.document.Document
import com.moban.model.data.project.Project
import com.moban.model.response.BaseDataList


/**
 * Created by H. Len Vo on 5/25/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class HomeDealDetail: BaseModel() {
    var type: String = ""
    var title: String = ""

    var projects: List<Project> = ArrayList()
    var banners: List<Document> = ArrayList()
    var deals: BaseDataList<Document> = BaseDataList()

    @SerializedName("endTime")
    var endTime: Double = 0.0

}
