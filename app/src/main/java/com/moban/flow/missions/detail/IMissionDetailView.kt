package com.moban.flow.missions.detail

import com.moban.model.data.user.Mission
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/24/18.
 */
interface IMissionDetailView : BaseMvpView {
    fun bindingMissionDetail(mission: Mission)
}
