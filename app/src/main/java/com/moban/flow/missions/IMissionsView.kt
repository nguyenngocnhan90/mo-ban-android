package com.moban.flow.missions

import com.moban.model.response.user.ListMissions
import com.moban.mvp.BaseMvpView

interface IMissionsView : BaseMvpView {
    fun showMissions(missions: ListMissions)
}
