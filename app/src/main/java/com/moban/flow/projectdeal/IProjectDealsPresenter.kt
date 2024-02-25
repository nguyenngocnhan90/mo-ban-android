package com.moban.flow.projectdeal

import com.moban.mvp.BaseMvpPresenter


//  Created by H. Len Vo on 6/16/2020.
//  Copyright © 2019 H. Len Vo. All rights reserved.
//

interface IProjectDealsPresenter : BaseMvpPresenter<IProjectDealsView> {
    fun getDeals(projectId: Int, page: Int)
}
