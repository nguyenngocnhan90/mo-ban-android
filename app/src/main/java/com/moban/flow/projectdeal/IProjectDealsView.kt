package com.moban.flow.projectdeal

import com.moban.model.data.document.Document
import com.moban.model.response.BaseDataList
import com.moban.mvp.BaseMvpView


//  Created by H. Len Vo on 6/16/2020.
//  Copyright © 2019 H. Len Vo. All rights reserved.
//

interface IProjectDealsView: BaseMvpView {
    fun bindingDealsList(deals: BaseDataList<Document>)
}
