package com.moban.flow.searchpartner

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 5/11/18.
 */
interface ISearchPartnerPresenter : BaseMvpPresenter<ISearchPartnerView> {
    fun searchPartner(keyword: String)
}
