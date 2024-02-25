package com.moban.model.response.coin

import com.moban.model.data.Paging
import com.moban.model.data.user.LinkPointTransaction

/**
 * Created by LenVo on 3/29/18.
 */
class ListLinkPointTransactions {
    var list: List<LinkPointTransaction> = ArrayList()
    var paging: Paging? = null
}
