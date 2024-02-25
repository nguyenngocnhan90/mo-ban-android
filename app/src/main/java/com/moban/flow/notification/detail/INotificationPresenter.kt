package com.moban.flow.notification.detail

import com.moban.mvp.BaseMvpPresenter

interface INotificationPresenter: BaseMvpPresenter<INotificationDetailView> {
    fun loadNotification(id: Int)
}
