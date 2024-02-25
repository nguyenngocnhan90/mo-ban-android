package com.moban.flow.profile


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.widget.ShareDialog
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.user.BadgeAdapter
import com.moban.enum.LinkmartMainCategory
import com.moban.flow.badges.BadgeDetailActivity
import com.moban.flow.cointransfer.history.CoinHistoryActivity
import com.moban.flow.linkmart.LinkmartActivity
import com.moban.flow.missions.MissionsActivity
import com.moban.flow.notification.NotificationActivity
import com.moban.flow.profiledetail.IProfileDetailPresenter
import com.moban.flow.profiledetail.IProfileDetailView
import com.moban.flow.profiledetail.ProfileDetailActivity
import com.moban.flow.profiledetail.ProfileDetailPresenterIml
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.reward.RewardActivity
import com.moban.flow.salary.SalaryActivity
import com.moban.flow.searchpartner.SearchPartnerActivity
import com.moban.flow.signin.SignInActivity
import com.moban.handler.BadgeItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Post
import com.moban.model.data.user.Badge
import com.moban.model.data.user.User
import com.moban.model.response.LoginResponse
import com.moban.model.response.badge.ListBadgeResponse
import com.moban.mvp.BaseMvpActivity
import com.moban.network.service.ImageService
import com.moban.network.service.UserService
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.nav.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : BaseMvpActivity<IProfileDetailView, IProfileDetailPresenter>(), IProfileDetailView {
    override var presenter: IProfileDetailPresenter = ProfileDetailPresenterIml()

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)
    private val photoService = retrofit?.create(ImageService::class.java)

    var badgeAdapter: BadgeAdapter? = null

    private var noNetworkDialog: Dialog? = null
    private var dialog: Dialog? = null
    private lateinit var user: User

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_profile
    }

    override fun initialize(savedInstanceState: Bundle?) {
        profile_nav.nav_tvTitle.visibility = View.INVISIBLE
        profile_nav.nav_imgSearch.visibility = View.INVISIBLE
        profile_nav.nav_profile.visibility = View.INVISIBLE
        profile_nav.nav_imgQRCode.visibility = View.INVISIBLE
        profile_nav.nav_imgSearch.visibility = View.INVISIBLE
        profile_nav.nav_tvSearchPartner.visibility = View.VISIBLE
        profile_nav.nav_tvSearchProject.visibility = View.GONE
        profile_nav.nav_notification.visibility = View.VISIBLE

        profile_nav.nav_tvSearchPartner.setOnClickListener {
            SearchPartnerActivity.start(this)
        }

        profile_nav.nav_notification.setOnClickListener {
            NotificationActivity.start(this)
        }

        profile_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        LocalStorage.user()?.let {
            user = it

            val isAnonymous = user.isAnonymous()
            if (user.isOutsideAgent || isAnonymous) {
                configViewForOutsideAgent()

                if (isAnonymous) {
                    account_btn_profile_info.alpha = 0.2F
                    account_btn_deals.alpha = 0.2F
                }
            }

            disableSomeMenus()

            account_view_info.visibility = if (isAnonymous) View.INVISIBLE else View.VISIBLE
            account_view_login.visibility = if (!isAnonymous) View.INVISIBLE else View.VISIBLE
            account_recycler_badge.visibility = if (isAnonymous) View.INVISIBLE else View.VISIBLE
        }

        setFonts()
        initRecyclerView()

        // Load data
        getBadges()
        getProfile()

        // Set menu actions
        setMenuActions()

        account_refresh_layout.setColorSchemeResources(R.color.colorAccent)

        if (!user.isAnonymous()) {
            account_refresh_layout.setOnRefreshListener {
                getBadges()
                getProfile()
            }
        } else {
            account_refresh_layout.setOnRefreshListener {
                account_refresh_layout.isRefreshing = false
            }

            account_view_login.setOnClickListener {
                showViewToLogin()
            }

            account_btn_login.setOnClickListener {
                showViewToLogin()
            }

            account_btn_register.setOnClickListener {
                showViewToLogin()
            }
        }
    }

    private fun showViewToLogin() {
        SignInActivity.start(this, this)
    }

    private fun configViewForOutsideAgent() {
        val arrayViewHidden = arrayOf(account_btn_reward, account_btn_link_reward, account_btn_mission,
                account_btn_salary, account_btn_linkbook, account_btn_linkhub, account_btn_linkmart)
        arrayViewHidden.forEach {
            it.alpha = 0.2F
        }
    }

    private fun disableSomeMenus() {
//        val arrayViewHidden = arrayOf(account_btn_linkmart, account_btn_linkhub, account_btn_linkbook)
//        arrayViewHidden.forEach {
//            it.visibility = View.GONE
//        }
        account_list_related_service.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        getProfile()
    }

    private fun getFeeds(isRefreshing: Boolean) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                getFeeds(isRefreshing)
            })
            return
        }


        if (!::user.isInitialized) {
            LocalStorage.user()?.let {
                user = it
            }
        }
    }

    private fun dismissNetworkDialog() {
        noNetworkDialog?.dismiss()
    }

    fun fillProfile() {
        LocalStorage.user()?.let { it ->
            user = it
            LHPicasso.loadImage(it.avatar, account_image)
            account_tv_name?.text = it.name
            val currentCoin = it.currentCoinString() + " Điểm"
            account_tv_coin?.text = currentCoin
            account_tv_role?.text = it.role

            val validatedRankName = it.validateRankName()
            account_tv_rank?.text = validatedRankName
            account_tv_rank?.visibility = if (validatedRankName.isEmpty()) View.GONE else View.VISIBLE
            account_tv_rank.setTextColor(Color.WHITE)

            val drawable = this.getDrawable(R.drawable.background_profile_half_round_buttercup)
            drawable?.setColorFilter(Color.parseColor(it.rankColor()), PorterDuff.Mode.SRC_IN)
            account_tv_rank?.background = drawable

            account_image?.let {
                LHPicasso.loadImage(user.avatar, account_image)
            }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        account_recycler_badge.layoutManager = layoutManager

        badgeAdapter = BadgeAdapter()
        account_recycler_badge.adapter = badgeAdapter

        val context = this
        badgeAdapter?.listener = object : BadgeItemHandler {
            override fun onClicked(badge: Badge) {
                BadgeDetailActivity.start(context, badge)
            }
        }
    }

    private fun downloadPhoto(photo: Photo) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                downloadPhoto(photo)
            })
            return
        }

        if (!Permission.checkPermissionWriteExternalStorage(this)) {
            return
        }

        val context = this

        photoService?.download(photo.photo_Link)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.let {
                    val fileName = FileUtil.getFileNameFromURL(photo.photo_Link)
                    BitmapUtil.saveImage(context, it.byteStream(), fileName)
                }
            }
        })
    }

    /**
     * Navigation
     */

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

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun setMenuActions() {
        account_btn_salary.setOnClickListener {
            LocalStorage.user()?.let {
                if (!it.isOutsideAgent && !it.isAnonymous()) {
                    SalaryActivity.start(this)
                }
            }
        }

        account_btn_reward.setOnClickListener { _ ->
            LocalStorage.user()?.let {
                if (!it.isOutsideAgent && !it.isAnonymous()) {
                    RewardActivity.start(this, it)
                }
            }
        }

        account_btn_deals.setOnClickListener { _ ->
            LocalStorage.user()?.let {
                if (!it.isAnonymous()) {
                    ListReservationActivity.start(this, user)
                }
            }
        }

        account_btn_mission.setOnClickListener {
            LocalStorage.user()?.let {
                if (!it.isOutsideAgent && !it.isAnonymous()) {
                    MissionsActivity.start(this)
                }
            }
        }

        account_btn_profile_info.setOnClickListener { _ ->
            LocalStorage.user()?.let {
                ProfileDetailActivity.start(this, it)
            }
        }

        account_btn_link_reward.setOnClickListener {
            LocalStorage.user()?.let {
                if (!it.isOutsideAgent && !it.isAnonymous()) {
                    LinkmartActivity.start(this, LinkmartMainCategory.linkReward.value)
                }
            }
        }

        account_btn_linkmart.setOnClickListener {
//            LocalStorage.user()?.let {
//                if (!it.isOutsideAgent && !it.isAnonymous()) {
//                    LinkmartActivity.start(this, LinkmartMainCategory.linkMart.value)
//                }
//            }
        }

        account_btn_linkhub.setOnClickListener {
//            LocalStorage.user()?.let {
//                if (!it.isOutsideAgent && !it.isAnonymous()) {
//                    LinkmartActivity.start(this, LinkmartMainCategory.linkHub.value)
//                }
//            }
        }

        account_btn_linkbook.setOnClickListener {
            LocalStorage.user()?.let {
                if (!it.isOutsideAgent && !it.isAnonymous()) {
                    LinkmartActivity.start(this, LinkmartMainCategory.linkBook.value)
                }
            }
        }

        account_view_coin.setOnClickListener {
            LocalStorage.user()?.let {
                if (!it.isOutsideAgent && !it.isAnonymous()) {
                    CoinHistoryActivity.start(this)
                }
            }
        }
    }

    private fun setFonts() {
        arrayOf(account_tv_role, account_tv_coin)
                .forEach {
                    it.typeface = Font.regularTypeface(this)
                }

        arrayOf(account_menu_tv_reward, account_menu_tv_deal, account_menu_tv_mission,
                account_menu_tv_salary, account_menu_tv_info, account_menu_tv_gift,
                account_menu_tv_linkbook, account_menu_tv_linkhub, account_menu_tv_linkmart)
                .forEach {
                    it.typeface = Font.boldTypeface(this)
                }

        account_tv_name.typeface = Font.boldTypeface(this)
        account_tv_coin_title.typeface = Font.mediumTypeface(this)
    }


    /**
     * Call API
     */

    private fun getProfile() {
        if (!NetworkUtil.hasConnection(this)) {
            return
        }

        userService?.profile()?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                account_refresh_layout?.isRefreshing = false
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                account_refresh_layout?.isRefreshing = false

                response?.body()?.data?.let {
                    LocalStorage.saveUser(it)
                    LHApplication.instance.initLinkmartNetComponent()
                    fillProfile()
                }
            }
        })
    }

    private fun getBadges() {
        if (!NetworkUtil.hasConnection(this)) {
            return
        }
        LocalStorage.user()?.let { user ->
            userService?.myBadges(user.id)?.enqueue(object : Callback<ListBadgeResponse> {
                override fun onFailure(call: Call<ListBadgeResponse>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<ListBadgeResponse>?, response: Response<ListBadgeResponse>?) {
                    response?.body()?.data?.list?.let {
                        badgeAdapter?.badges = it

                        LocalStorage.saveBadges(it)
                    }
                }
            })
        }
    }

    override fun bindingBadges(badges: List<Badge>) {

    }
}
