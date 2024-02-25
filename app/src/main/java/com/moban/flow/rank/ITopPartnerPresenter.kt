package com.moban.flow.rank

import com.moban.enum.PartnerFilterType
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 6/26/18.
 */
interface ITopPartnerPresenter : BaseMvpPresenter<ITopPartnerView> {
    fun loadTopPartner(page: Int, partnerType: PartnerFilterType)
}
