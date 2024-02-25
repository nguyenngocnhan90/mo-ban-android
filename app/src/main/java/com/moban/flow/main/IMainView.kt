package com.moban.flow.main

import com.moban.model.response.notification.ListNotification
import com.moban.mvp.BaseMvpView

/**
 * Created by thangnguyen on 12/18/17.
 */
interface IMainView : BaseMvpView {
    fun bindingNotifications(notifications: ListNotification)
}
