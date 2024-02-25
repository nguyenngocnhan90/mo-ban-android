package com.moban.flow.missions

import com.moban.mvp.BaseMvpPresenter

interface IMissionsPresenter : BaseMvpPresenter<IMissionsView> {
    fun getMissions()
}
