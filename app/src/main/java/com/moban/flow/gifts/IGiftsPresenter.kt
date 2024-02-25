package com.moban.flow.gifts

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/11/18.
 */
interface IGiftsPresenter : BaseMvpPresenter<IGiftsView> {
    fun loadGifts(page: Int)
}
