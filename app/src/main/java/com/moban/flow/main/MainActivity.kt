package com.moban.flow.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.youtube.player.YouTubePlayer
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.moban.LHApplication
import com.moban.R
import com.moban.constant.ServerURL
import com.moban.enum.DealFilter
import com.moban.enum.GACategory
import com.moban.enum.NotificationStatus
import com.moban.enum.NotificationType
import com.moban.flow.activity.ActivityFragment
import com.moban.flow.cointransfer.CoinTransferActivity
import com.moban.flow.deals.dealdetail.DealDetailActivity
import com.moban.flow.deals.reviewdeal.ReviewDealActivity
import com.moban.flow.home.NewHomeFragment
import com.moban.flow.linkmart.detail.LinkmartDetailActivity
import com.moban.flow.missions.detail.MissionDetailActivity
import com.moban.flow.more.MoreFragment
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.notification.NotificationActivity
import com.moban.flow.notification.detail.NotificationDetailActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.profile.ProfileActivity
import com.moban.flow.projectdetail.bookingdetail.BookingDetailActivity
import com.moban.flow.projects.ProjectsFragment
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.flow.qrcode.QRCodeActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.review.ReviewActivity
import com.moban.flow.reward.RewardActivity
import com.moban.flow.searchpartner.SearchPartnerActivity
import com.moban.flow.signin.SignInActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.flow.youtube.YoutubeVideoFragment
import com.moban.handler.IYoutubeContainer
import com.moban.handler.MainQuickButtonHandle
import com.moban.helper.LocalStorage
import com.moban.model.data.notification.Notification
import com.moban.model.data.popup.Popup
import com.moban.model.data.post.Post
import com.moban.model.response.notification.ListNotification
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogBottomSheetUtil
import com.moban.util.DialogUtil
import com.moban.util.Permission
import com.moban.util.ViewUtil
import com.moban.view.widget.TintableImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav.view.*


class MainActivity : BaseMvpActivity<IMainView, IMainPresenter>(), IYoutubeContainer, IMainView {
    override var presenter: IMainPresenter = MainPresenterIml()
    private var dialog: Dialog? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            context.startActivity(intent)
        }
    }

    private var flVideoContainer: FrameLayout? = null
    private var youtubeVideoFragment: YoutubeVideoFragment? = null
    private var viewYoutubePos = -1
    private var isOutsideAgent = false

    private var PAGE_INDEX_FEED = 0
    private var PAGE_INDEX_PROJECT = 1
    private var PAGE_INDEX_PROFILE = 2
    private var PAGE_INDEX_ACTIVITY = 3
    private var PAGE_INDEX_MORE = 4

    private var centerActionDialog: Dialog? = null
    private var loginRequiredDialog: Dialog? = null

    private lateinit var pagerAdapter: FragmentPagerItemAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Main Activity", GACategory.HOME)
        LocalStorage.user()?.let {
            isOutsideAgent = it.isOutsideAgent
        }

        val homeTitle = getString(R.string.tab_home)
        val profileTitle = getString(R.string.tab_profile)
        val projectTitle = getString(R.string.tab_project)
        val moreTitle = getString(R.string.tab_more)
        val activityTitle = getString(R.string.tab_activity)

        ViewUtil.setMarginTop(main_tbToolbar, ViewUtil.getStatusBarHeight(this))
        main_tbToolbar_nav.nav_imgBack.visibility = View.INVISIBLE
        main_tbToolbar_nav.nav_tvTitle.text = homeTitle

        val params = main_tbToolbar_nav.nav_imgQRCode.layoutParams as RelativeLayout.LayoutParams
        params.marginStart = 0

        main_tbToolbar_nav.nav_imgQRCode.layoutParams = params
        main_tbToolbar_nav.nav_imgQRCode.visibility = View.VISIBLE
        main_tbToolbar_nav.nav_imgQRCode.setOnClickListener {
            QRCodeActivity.start(this)
        }

        val pages = FragmentPagerItems(this)
        pages.add(FragmentPagerItem.of(homeTitle, NewHomeFragment::class.java))
        pages.add(FragmentPagerItem.of(projectTitle, ProjectsFragment::class.java))
        pages.add(FragmentPagerItem.of(profileTitle, ActivityFragment::class.java))
        pages.add(FragmentPagerItem.of(activityTitle, ActivityFragment::class.java))
        pages.add(FragmentPagerItem.of(moreTitle, MoreFragment::class.java))

        pagerAdapter = FragmentPagerItemAdapter(supportFragmentManager, pages)
        main_vpViewPager.adapter = pagerAdapter

        val inflater = LayoutInflater.from(this)
        main_tlTabLayout.setCustomTabView { container, position, adapter ->
            val itemView = inflater.inflate(R.layout.item_main_tab, container, false)
            val text = itemView.findViewById(R.id.txtTabText) as TextView
            text.text = adapter.getPageTitle(position)
            val icon = itemView.findViewById(R.id.imgTabIcon) as TintableImageView
            when (position) {
                PAGE_INDEX_FEED -> icon.setImageResource(R.drawable.tab_home)
                PAGE_INDEX_PROJECT -> icon.setImageResource(R.drawable.tab_project)
                PAGE_INDEX_PROFILE -> icon.setImageResource(R.drawable.tab_profile)
                PAGE_INDEX_ACTIVITY -> icon.setImageResource(R.drawable.tabbar_activity)
                PAGE_INDEX_MORE -> icon.setImageResource(R.drawable.tab_more)
                else -> throw IllegalStateException("Invalid position: $position")
            }

            itemView.visibility = if (position == PAGE_INDEX_PROFILE) View.INVISIBLE else View.VISIBLE
            itemView
        }
        main_tlTabLayout.setViewPager(main_vpViewPager)
        main_vpViewPager.offscreenPageLimit = pages.count()
        main_vpViewPager.isPagingEnabled = false

        main_tbToolbar_nav.nav_tvTitle.visibility = View.INVISIBLE
        main_tbToolbar_nav.nav_imgSearch.visibility = View.VISIBLE
        main_tbToolbar_nav.nav_profile.visibility = View.VISIBLE
        main_tbToolbar_nav.nav_tvSearchPartner.visibility = View.GONE
        main_tbToolbar_nav.nav_tvSearchProject.visibility = View.GONE

        main_vpViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val isProfile = position == PAGE_INDEX_PROFILE

                main_tbToolbar_nav.nav_tvSearchPartner.visibility = if (isProfile) View.VISIBLE else View.GONE
                main_tbToolbar_nav.nav_imgSearch.visibility = if (isProfile) View.INVISIBLE else View.VISIBLE
                main_tbToolbar_nav.nav_imgQRCode.visibility = if (isProfile) View.INVISIBLE else View.VISIBLE
            }
        })

        main_tbToolbar_nav.nav_profile.setOnClickListener {
            ProfileActivity.start(this)
        }

        main_tbToolbar_nav.nav_imgSearch.setOnClickListener {
            SearchProjectActivity.start(this)
        }

        main_tbToolbar_nav.nav_tvSearchPartner.setOnClickListener {
            SearchPartnerActivity.start(this)
        }

        main_tbToolbar_nav.nav_notification.visibility = View.VISIBLE
        main_tbToolbar_nav.nav_notification.setOnClickListener {
            NotificationActivity.start(this)
        }

        val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
        val context = this

        main_tab_center_button.bringToFront()
        main_tab_center_button.setOnClickListener {
            centerActionDialog = DialogBottomSheetUtil.showDialogCenterQuickAction(this, object : MainQuickButtonHandle {
                override fun requestProduct() {
                    WebViewActivity.start(context, getString(R.string.request_product), ServerURL.requestProductURL, oneTimeToken = true)
                    centerActionDialog?.dismiss()
                }

                override fun callSupport() {
                    val phone = getString(R.string.phone_support)
                    Permission.openCalling(context, phone)
                    centerActionDialog?.dismiss()
                }

                override fun referPartner() {
                    if (isAnonymous) {
                        loginRequiredDialog = DialogUtil.showLoginRequiredDialog(context, View.OnClickListener {
                            SignInActivity.start(context, this@MainActivity)
                            loginRequiredDialog?.dismiss()
                        })
                    } else {
                        WebViewActivity.start(context, getString(R.string.refer_partner), ServerURL.referFriendURL, oneTimeToken = true)
                    }

                    centerActionDialog?.dismiss()
                }
            })
        }

        // Check to open saved notification
        checkOpenNotification()

        if (isAnonymous) {
            bindingNotifications(ListNotification())
        } else {
            presenter.loadNotifications()
        }

        presenter.loadPostTopics()
        presenter.loadPostFilterTopics()

        if (!isOutsideAgent) {
            presenter.loadLmartBrands()
            presenter.loadCategoriesLinkmart()
            presenter.loadCategoriesLinkbook()
            presenter.loadCategoriesLinkhub()
        }

        checkUnReviewDeals()
        checkPopupData()
    }

    private fun checkPopupData() {
        val popup = LHApplication.instance.lhCache.popup ?: return
        val titleBtn = if (popup.type.contains(NotificationType.REVIEW.value) ||
                popup.type.contains(NotificationType.REVIEW_DEAL.value)) getString(R.string.review_now) else
            getString(R.string.ok)

        var dialog: Dialog? = null
        val type = popup.type

        popup.review?.let { _ ->
            dialog = DialogUtil.showInfoDialog(this, "", popup.content, titleBtn,
                    View.OnClickListener {
                        dialog?.dismiss()
                        startReview(popup)
                    })
        } ?: run {
            dialog = DialogUtil.showPopupDialog(this, popup.content, popup.photo,
                    View.OnClickListener {
                        dialog?.dismiss()
                        startReview(popup)
                    })
        }
    }

    private fun startReview(popup: Popup) {
        val type = popup.type
        if (popup.review != null) {
            val review = popup.review!!
            if (type.contains(NotificationType.REVIEW.value)) {
                ReviewActivity.start(this, review)
            } else if (type.contains(NotificationType.REVIEW_DEAL.value)) {
                ReviewDealActivity.start(this, popup.object_id)
            }
        } else {
            val notification = Notification()
            notification.id = popup.id
            notification.type = popup.type
            notification.linked_Object_Id = popup.object_id
            showActivityFromNotification(notification)
        }
    }

    private fun checkUnReviewDeals() {
        LocalStorage.user()?.let {
            main_tv_unreview_deals.visibility = if (it.unreview_Deal_Count > 0) View.VISIBLE else
                View.GONE
            if (it.unreview_Deal_Count > 0) {
                val title = getString(R.string.you_has_deals_not_review_tabbar).replace("\$N", it.unreview_Deal_Count.toString())
                main_tv_unreview_deals.text = title
                val user = it
                main_tv_unreview_deals.setOnClickListener {
                    ListReservationActivity.start(this@MainActivity, user, DealFilter.REVIEWABLE)
                }
            }
        }
    }

    private fun checkOpenNotification() {
        LocalStorage.savedNotification()?.let { it ->
            showActivityFromNotification(it)
            clearSavedNotification()
        }
    }

    private fun showActivityFromNotification(notification: Notification) {
        if (notification.type.isNullOrEmpty()) {
            return
        }
        val type = notification.type
        val objectId = notification.linked_Object_Id
        val activity = this@MainActivity
        if (type == NotificationType.FEED.value || type.contains(NotificationType.FEED.value + "/")) {
            openPost(objectId)
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
        } else if (type == NotificationType.REWARD_DETAIL.value || type.contains(NotificationType.REWARD_DETAIL.value + "/")) {
            LinkmartDetailActivity.startGift(activity, objectId)
        } else if (type == NotificationType.TRANSACTION.value || type.contains(NotificationType.TRANSACTION.value + "/")) {
            LocalStorage.user()?.let { user ->
                RewardActivity.start(activity, user)
            }
        } else if (type.isNotEmpty() || notification.id > 0) {
            NotificationDetailActivity.start(activity, notification.id)
        }
    }

    private fun openPost(postId: Int) {
        PostDetailActivity.start(this, postId)
    }

    private fun clearSavedNotification() {
        LocalStorage.saveNotification(null)
    }

    /**
     * Implement youtube
     */

    override fun sendLoggingEvent(action: String) {

    }

    // YouTube video
    private fun initVideoFrameLayout(youtubeVideoFragment: YoutubeVideoFragment) {
        fragmentManager.beginTransaction()
                .replace(getFlVideoContainer().id, youtubeVideoFragment,
                        YoutubeVideoFragment.TAG)
                .commitAllowingStateLoss()
    }

    override fun getFlVideoContainer(): FrameLayout {
        if (flVideoContainer == null) {
            flVideoContainer = FrameLayout(this)

            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            flVideoContainer?.layoutParams = layoutParams
            flVideoContainer?.visibility = View.GONE
            flVideoContainer?.id = View.generateViewId()
        }

        return flVideoContainer!!
    }

    override fun getYoutubeVideoFragment(): YoutubeVideoFragment {
        if (youtubeVideoFragment == null) {
            youtubeVideoFragment = YoutubeVideoFragment.newInstance()
            youtubeVideoFragment?.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
            youtubeVideoFragment?.setShowFullscreenButton(false)

            initVideoFrameLayout(youtubeVideoFragment!!)
        }

        return youtubeVideoFragment!!
    }

    override fun getViewYoutubePos(): Int {
        return viewYoutubePos
    }

    override fun setViewYoutubePos(position: Int) {
        viewYoutubePos = position
    }

    override fun handleOnVideoFullScreen(isFullScreen: Boolean) {
    }

    override fun checkYouTubeViewOverlay() {
        val playerLocation = IntArray(2)
        getFlVideoContainer().getLocationInWindow(playerLocation)
//        val x = playerLocation[0]
        val y = playerLocation[1]
        val pagerLocation = IntArray(2)
        main_vpViewPager.getLocationOnScreen(pagerLocation)
        val relativeY = y - pagerLocation[1]
        val topBarHeight = 45

        if (relativeY < topBarHeight) {
            val intent = Intent(NewHomeFragment.BROADCAST_SCROLL_REQUEST)
            intent.putExtra(NewHomeFragment.BUNDLE_SCROLL_REQUEST_VALUE, relativeY - topBarHeight)
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else if (relativeY + getFlVideoContainer().height > main_vpViewPager.height) {
            val intent = Intent(NewHomeFragment.BROADCAST_SCROLL_REQUEST)
            val value = relativeY + getFlVideoContainer().height - main_vpViewPager.height
            intent.putExtra(NewHomeFragment.BUNDLE_SCROLL_REQUEST_VALUE, value)
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    override fun releaseYouTubePlayer() {
        youtubeVideoFragment?.releasePlayer()

        youtubeVideoFragment = null
    }

    override fun onResume() {
        super.onResume()
        checkUnReviewDeals()
        ViewUtil.updateUnReadNotificationView(main_tbToolbar_nav.nav_unread_notification)
    }

    override fun bindingNotifications(notifications: ListNotification) {
        val unRead = notifications.unread_Count
        LocalStorage.saveUnReadNotification(unRead)

        main_tbToolbar_nav.nav_unread_notification.text = unRead.toString()
        main_tbToolbar_nav.nav_unread_notification.visibility = if (unRead == 0)
            View.INVISIBLE else View.VISIBLE

        //Check notification
        for (notification in notifications.list) {
            if (notification.is_Read == NotificationStatus.UNREAD.value && (notification.type == NotificationType.TRANSACTION_COIN.value
                            || notification.type.contains(NotificationType.TRANSACTION_COIN.value + "/"))) {
                showNotificationLixi(notification)
            }
        }
    }

    private fun showNotificationLixi(notification: Notification) {
        val message = if (notification.body == null) "" else notification.body + "\n" + getString(R.string.want_resent_lixi)
        presenter.markAsRead(notification)
        dialog = DialogUtil.showConfirmDialog(this, false, getString(R.string.congrats), message,
                getString(R.string.resend_lixi), getString(R.string.no),
                View.OnClickListener {
                    dialog?.dismiss()
                    CoinTransferActivity.start(this)
                }, null)
    }

    fun insertNewPostOnMyFeeds(newPost: Post) {

    }

    fun showProjectTabBar() {
        main_vpViewPager.setCurrentItem(PAGE_INDEX_PROJECT)
    }

    fun insertNewPostOnNewFeeds(newPost: Post) {
        val fragment = pagerAdapter.getPage(PAGE_INDEX_FEED) ?: null
        fragment?.let {
            val homeFragment = it as NewHomeFragment
            //TODO: New action on new home
//            homeFragment.insertNewPost(newPost)
        }
    }
}
