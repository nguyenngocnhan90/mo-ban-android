package com.moban.flow.main

import com.moban.model.data.notification.Notification
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by thangnguyen on 12/18/17.
 */
interface IMainPresenter : BaseMvpPresenter<IMainView> {
    fun loadNotifications()
    fun loadLmartBrands()
    fun loadCategoriesLinkmart()
    fun loadCategoriesLinkhub()
    fun loadCategoriesLinkbook()
    fun markAsRead(notification: Notification)
    fun loadPostTopics()
    fun loadPostFilterTopics()
}
