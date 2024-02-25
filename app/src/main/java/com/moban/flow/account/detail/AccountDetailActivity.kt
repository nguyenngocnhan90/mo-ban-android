package com.moban.flow.account.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.feed.FeedAdapter
import com.moban.adapter.user.BadgeAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.enum.LinkmartMainCategory
import com.moban.enum.NewPostQuickAction
import com.moban.flow.badges.BadgeDetailActivity
import com.moban.flow.cointransfer.history.CoinHistoryActivity
import com.moban.flow.linkmart.list.LinkmartListActivity
import com.moban.flow.missions.MissionsActivity
import com.moban.flow.notification.NotificationActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.profiledetail.ProfileDetailActivity
import com.moban.flow.qrcode.QRCodeActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.reward.RewardActivity
import com.moban.flow.salary.SalaryActivity
import com.moban.flow.searchpartner.SearchPartnerActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.BadgeItemHandler
import com.moban.handler.FeedItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.helper.LocalStorage
import com.moban.helper.ShareHelper
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.data.user.Badge
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.AppUtil
import com.moban.util.DialogUtil
import com.moban.util.LHPicasso
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_account_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav.view.*

class AccountDetailActivity : BaseMvpActivity<IAccountDetailView, IAccountDetailPresenter>(), IAccountDetailView {
    override var presenter: IAccountDetailPresenter = AccountDetailPresenterIml()

    private var userId: Int = 0
    private var user: User? = null
    private var badgeAdapter: BadgeAdapter = BadgeAdapter()
    private var feedAdapter: FeedAdapter = FeedAdapter()
    private var lowestId = 0
    private var dialog: Dialog? = null

    companion object {
        const val BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID"
        fun start(context: Context, userId: Int) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USER_ID, userId)

            val intent = Intent(context, AccountDetailActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_account_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras

        if (intent.hasExtra(BUNDLE_KEY_USER_ID)) {
            userId = bundle?.getSerializable(BUNDLE_KEY_USER_ID) as Int

            LocalStorage.user()?.let {
                if (it.id == userId) {
                    user = it
                    loadProfile(it)
                } else {
                    presenter.loadUserDetail(userId)
                }
            }

            presenter.loadBadges(userId)
            getFeeds(true)
        }

        initToolBar()

        initRecyclerView()

        setMenuActions()

        account_detail_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        account_detail_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        setGAScreenName("Account Detail $userId", GACategory.ACCOUNT)
    }

    private fun initToolBar() {
        account_detail_nav.nav_tvTitle.visibility = View.GONE
        account_detail_nav.nav_tvSearchPartner.visibility = View.VISIBLE
        account_detail_nav.nav_tvSearchPartner.setOnClickListener {
            SearchPartnerActivity.start(this)
        }

        account_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        account_detail_nav.nav_notification.visibility = View.VISIBLE
        account_detail_nav.nav_notification.setOnClickListener {
            NotificationActivity.start(this)
        }

        account_detail_nav.nav_imgQRCode.visibility = View.GONE
        account_detail_nav.nav_imgQRCode.setOnClickListener {
            QRCodeActivity.start(this)
        }
        account_detail_nav.nav_imgQRCode.requestLayout()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        account_detail_recycler_badge.layoutManager = layoutManager

        badgeAdapter = BadgeAdapter()
        account_detail_recycler_badge.adapter = badgeAdapter

        badgeAdapter.listener = object : BadgeItemHandler {
            override fun onClicked(badge: Badge) {
                BadgeDetailActivity.start(this@AccountDetailActivity, badge)
            }
        }

        val linearLayoutManager = LinearLayoutManager(this)
        profile_recycler_view_feeds.layoutManager = linearLayoutManager
        profile_recycler_view_feeds.adapter = feedAdapter
        profile_recycler_view_feeds.isNestedScrollingEnabled = false
        LocalStorage.user()?.let {
            feedAdapter.user = it
            feedAdapter.isCanPost = false
        }

        val context = this

        feedAdapter.listener = object : FeedItemHandler {
            override fun onReact(post: Post) {
                if (post.is_Liked) {
                    presenter.unlike(post)
                } else {
                    presenter.like(post)
                }
            }

            override fun onComment(post: Post) {
                PostDetailActivity.start(this@AccountDetailActivity,
                        this@AccountDetailActivity, post, true)
            }

            override fun onViewFeed(post: Post) {
                PostDetailActivity.start(this@AccountDetailActivity,
                        this@AccountDetailActivity, post, false)
            }

            override fun onFollow(post: Post) {
                if (post.is_Followed) {
                    presenter.unfollow(post.id)
                } else {
                    presenter.follow(post.id)
                }
            }

            override fun onShare(post: Post, bitmaps: List<Bitmap>) {
                Util.saveToClipboard(this@AccountDetailActivity, post.getFullContent())
                ShareHelper.sharePost(context, post)
            }

            override fun onExpandContent(post: Post) {
                post.isExpanded = true
                feedAdapter.replacePostItem(post)
            }

            override fun onNewPost(quickAction: NewPostQuickAction) {

            }

            override fun onViewProfileDetail(userId: Int) {
                start(this@AccountDetailActivity, userId)
            }

            override fun onDownloadPhoto(photo: Photo) {
                presenter.downloadPhoto(photo)
            }

            override fun onClickURL(url: String) {
                WebViewActivity.start(this@AccountDetailActivity, getString(R.string.app_name), url)
            }

            override fun onDelete(post: Post) {
            }

            override fun onEdit(post: Post) {
            }

            override fun onReplyComment(comment: Comment) {

            }
        }

        feedAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                getFeeds(false)
            }
        }
    }

    private fun refreshData() {
        lowestId = 0
        getFeeds(true)
    }

    private fun getFeeds(isRefreshing: Boolean) {
        val currentId = if (lowestId > 0 && !isRefreshing) lowestId else null
        presenter.loadFeeds(userId, currentId)
    }

    override fun bindingPosts(feeds: List<Post>, canLoadMore: Boolean, isRefreshing: Boolean, lowestId: Int) {
        account_detail_refresh_layout.isRefreshing = false
        this.lowestId = lowestId
        if (isRefreshing || feedAdapter.isDataEmpty) {
            feedAdapter.setPosts(feeds, canLoadMore)
        } else {
            feedAdapter.appendPosts(feeds, canLoadMore)
        }
    }

    private fun sharePost(post: Post, bitmaps: List<Bitmap>) {
        val context = this

        if (!AppUtil.hasFacebookApp(context)) {
            dialog = DialogUtil.showErrorDialog(context,
                    "Bạn chưa có Facebook app",
                    "Bạn hãy cài đặt Facebook app để chia sẻ bài viết nhé.",
                    getString(R.string.ok),
                    View.OnClickListener {
                        dialog?.dismiss()
                        AppUtil.openAppPackage(context, Constant.FACEBOOK_APP_PACKAGE_NAME)
                    })
            return
        }

        if (post.hasVideo()) {
            val link = "https://www.youtube.com/watch?v=" + post.youtubeId

            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(link))
                    .build()

            val dialog = ShareDialog(this)
            initCallbackShareFacebook(dialog, post)
            dialog.show(linkContent, ShareDialog.Mode.AUTOMATIC)
        } else {
            val content = SharePhotoContent.Builder()
            bitmaps.forEach {
                val photo = SharePhoto.Builder().setBitmap(it).build()
                content.addPhoto(photo)
            }

            val shareDialog = ShareDialog(this)
            initCallbackShareFacebook(shareDialog, post)
            shareDialog.show(content.build(), ShareDialog.Mode.NATIVE)
        }
    }

    private fun initCallbackShareFacebook(shareDialog: ShareDialog, post: Post) {
        shareDialog.registerCallback(LHApplication.instance.getFbManager(), object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result?) {
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })
    }

    override fun reloadPost(post: Post) {
        feedAdapter.reloadPost(post)
    }

    override fun showFollowedMessage() {
        DialogUtil.showInfoDialog(this,
                getString(R.string.feed_you_did_follow),
                getString(R.string.feed_followed_msg),
                getString(R.string.ok), null)
    }

    private fun loadProfile(user: User) {

        LocalStorage.user()?.let { it ->
            account_detail_admin_view.visibility = if (it.isAdmin() || it.id == user.id) View.VISIBLE else View.GONE
            account_detail_btn_salary.setOnClickListener {
                SalaryActivity.start(this@AccountDetailActivity, user)
            }

            account_detail_btn_mission.alpha = if (it.id == user.id && !user.isOutsideAgent) 1F else 0.2F
            account_detail_btn_gift.alpha = if (it.id == user.id && !user.isOutsideAgent) 1F else 0.2F
            if (it.id == user.id && !user.isOutsideAgent) {
                account_detail_btn_mission.setOnClickListener {
                    MissionsActivity.start(this@AccountDetailActivity)
                }

                account_detail_btn_gift.setOnClickListener {
                    LinkmartListActivity.start(this@AccountDetailActivity, LinkmartMainCategory.linkReward.value)
                }
            }
            account_detail_btn_reward.alpha = if (!it.isOutsideAgent) 1F else 0.2F

            account_detail_img_coin.visibility = if(it.id == user.id) View.VISIBLE else View.GONE
        }

        account_detail_tv_name.text = user.name
        val currentCoin = user.currentCoinString() + " Điểm"
        account_detail_tv_coin.text = currentCoin
        account_detail_tv_role.text = user.role

        val validatedRankName = user.validateRankName()
        account_detail_tv_rank?.text = validatedRankName
        account_detail_tv_rank?.visibility = if (validatedRankName.isEmpty()) View.GONE else View.VISIBLE

        LHPicasso.loadImage(user.avatar, account_detail_image)

        val drawable = getDrawable(R.drawable.background_profile_half_round_buttercup)
        drawable?.setColorFilter(Color.parseColor(user.rankColor()), PorterDuff.Mode.SRC_IN)
        account_detail_tv_rank.background = drawable
    }

    private fun setMenuActions() {
        account_detail_btn_reward.setOnClickListener {
            LocalStorage.user()?.let { currentUser ->
                if (!currentUser.isOutsideAgent) {
                    user?.let {
                        RewardActivity.start(this, it)
                    }
                }
            }
        }

        account_detail_btn_deals.setOnClickListener {
            user?.let {
                ListReservationActivity.start(this, it)
            }
        }

        account_detail_btn_profile_info.setOnClickListener {
            user?.let {
                ProfileDetailActivity.start(this, it)
            }
        }

        account_detail_view_coin.setOnClickListener {
            user?.let {  user ->
                LocalStorage.user()?.let { currentUser ->
                    if (!currentUser.isOutsideAgent && currentUser.id == user.id) {
                        CoinHistoryActivity.start(this)
                    }
                }
            }
        }
    }

    override fun bindingUserBadges(badges: List<Badge>) {
        badgeAdapter.badges = badges
    }

    override fun bindingUserDetail(user: User) {
        this.user = user
        loadProfile(user)
    }
}
