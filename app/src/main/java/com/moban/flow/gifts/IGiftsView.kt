package com.moban.flow.gifts

import com.moban.model.response.gift.ListGifts
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/11/18.
 */
interface IGiftsView : BaseMvpView {
    fun bindingGifts(listGifts: ListGifts, canLoadMore: Boolean)
}
