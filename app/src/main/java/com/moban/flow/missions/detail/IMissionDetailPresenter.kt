package com.moban.flow.missions.detail

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/24/18.
 */
interface IMissionDetailPresenter : BaseMvpPresenter<IMissionDetailView> {
    fun loadMissionDetail(id: Int)
}
