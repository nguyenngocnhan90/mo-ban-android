package com.moban.model.response.coin

import com.moban.model.data.Paging
import com.moban.model.data.user.LhcTransaction

/**
 * Created by LenVo on 3/29/18.
 */
class ListTransactions {
    var list: List<LhcTransaction> = ArrayList()
    var paging: Paging? = null
}
