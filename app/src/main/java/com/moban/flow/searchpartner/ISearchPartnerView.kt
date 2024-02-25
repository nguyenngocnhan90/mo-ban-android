package com.moban.flow.searchpartner

import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 5/11/18.
 */
interface ISearchPartnerView : BaseMvpView {
    fun bindingSearchPartner(users: List<User>)
}
