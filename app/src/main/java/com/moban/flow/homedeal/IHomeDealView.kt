package com.moban.flow.homedeal

import com.moban.model.data.document.Document
import com.moban.model.data.homedeal.HomeDealDetail
import com.moban.mvp.BaseMvpView

/**
 * Created by lenvo on 6/13/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IHomeDealView : BaseMvpView {
    fun bindingDealDetail(dealDetail: HomeDealDetail)
    fun bindingDealsList(deals: List<Document>, canLoadMore: Boolean)
}
