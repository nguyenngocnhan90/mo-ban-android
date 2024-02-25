package com.moban.flow.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.adapter.notification.NotificationAdapter
import com.moban.enum.GACategory
import com.moban.enum.NotificationStatus
import com.moban.enum.NotificationType
import com.moban.flow.deals.dealdetail.DealDetailActivity
import com.moban.flow.deals.reviewdeal.ReviewDealActivity
import com.moban.flow.linkmart.detail.LinkmartDetailActivity
import com.moban.flow.missions.detail.MissionDetailActivity
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.notification.detail.NotificationDetailActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.projectdetail.bookingdetail.BookingDetailActivity
import com.moban.flow.review.ReviewActivity
import com.moban.flow.reward.RewardActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.NotificationItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.notification.Notification
import com.moban.model.response.notification.ListNotification
import com.moban.mvp.BaseMvpActivity
import com.moban.util.AppUtil
import com.moban.util.Permission
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.nav.view.*

class NotificationActivity : BaseMvpActivity<INotificationView, INotificationPresenter>(), INotificationView {
    override var presenter: INotificationPresenter = NotificationPresenterIml()

    private var page: Int = 1
    private val notificationAdapter = NotificationAdapter()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, NotificationActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("My Notifications", GACategory.NOTIFICATION)
        initToolbar()

        initRecycleView()

        presenter.loadNotifications(page)
    }

    private fun initToolbar() {
        notification_nav.nav_tvTitle.text = getString(R.string.notification)
        notification_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        notification_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        notification_refresh_layout.setOnRefreshListener {
            reloadNotifications()
        }

        notification_alert.visibility = if (Permission.checkPermissionNotification(this))
            View.GONE else View.VISIBLE

        notification_btn_ok.setOnClickListener {
            AppUtil.openSettingApp(this@NotificationActivity)
        }
    }

    private fun initRecycleView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        notification_recycler_view.layoutManager = layoutManager
        notification_recycler_view.adapter = notificationAdapter

        notificationAdapter.listener = object : NotificationItemHandler {
            override fun onClicked(notification: Notification) {
                if (notification.getNotificationStatus() == NotificationStatus.UNREAD) {
                    presenter.markAsRead(notification)
                    var unReadNotification = LocalStorage.getUnReadNotification()
                    if (unReadNotification > 0) {
                        unReadNotification--
                    }
                    LocalStorage.saveUnReadNotification(unReadNotification)
                }

                val type = notification.type

                if (type.isNullOrEmpty()) {
                    NotificationDetailActivity.start(this@NotificationActivity, notification)
                    return
                }

                val objectId = notification.linked_Object_Id
                val activity = this@NotificationActivity
                val user = LocalStorage.user()

                if (type == NotificationType.FEED.value || type.contains(NotificationType.FEED.value + "/")) {
                    PostDetailActivity.start(activity, objectId)
                } else if (type == NotificationType.PROJECT.value || type.contains(NotificationType.PROJECT.value + "/")) {
                    val isShowPolicyFirst = type.contains("/Policy")
                    ProjectDetailActivity.start(activity, objectId, isShowPolicyFirst)
                } else if (type == NotificationType.DEAL.value || type.contains(NotificationType.DEAL.value + "/")) {
                    DealDetailActivity.start(activity, objectId)
                } else if (type == NotificationType.BOOKING.value || type.contains(NotificationType.BOOKING.value + "/")) {
                    BookingDetailActivity.start(activity, objectId)
                } else if (type == NotificationType.MISSION.value || type.contains(NotificationType.MISSION.value + "/")) {
                    MissionDetailActivity.start(activity, objectId)
                } else if (type == NotificationType.REVIEW_DEAL.value || type.contains(NotificationType.REVIEW_DEAL.value + "/")) {
                    ReviewDealActivity.start(activity, objectId)
                } else if (type == NotificationType.REVIEW.value || type.contains(NotificationType.REVIEW.value + "/")) {
                    ReviewActivity.start(activity, objectId)
                }  else if (type == NotificationType.REWARD_DETAIL.value || type.contains(NotificationType.REWARD_DETAIL.value + "/")) {
                    LinkmartDetailActivity.startGift(activity, objectId)
                } else if (type == NotificationType.TRANSACTION.value || type.contains(NotificationType.TRANSACTION.value + "/")) {
                    user?.let {
                        RewardActivity.start(activity, user)
                    }
                } else {
                    NotificationDetailActivity.start(activity, notification)
                }
            }
        }

        notificationAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadNotifications(++page)
            }
        }
    }

    private fun reloadNotifications() {
        page = 1
        presenter.loadNotifications(page)
    }

    override fun bindingNotifications(notifications: ListNotification) {
        notification_refresh_layout.isRefreshing = false

        notifications.paging?.let {
            val canLoadMore = page < it.total
            if (page == 1) {
                notificationAdapter.setNotifications(notifications.list, canLoadMore)
            } else {
                notificationAdapter.appendNotifications(notifications.list, canLoadMore)
            }
        }
    }

    override fun markAsRead(notification: Notification) {
        notification.is_Read = NotificationStatus.READ.value
        notificationAdapter.reloadPost(notification)
    }
}
