package com.moban.flow.rank

import com.moban.enum.PartnerFilterType
import com.moban.model.response.user.ListUsers
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 6/26/18.
 */
interface ITopPartnerView : BaseMvpView {
    fun bindingTopPartner(listUsers: ListUsers, canLoadMore: Boolean, partnerType: PartnerFilterType)
}
