package com.moban.flow.profiledetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.moban.R
import com.moban.adapter.user.BadgeAdapter
import com.moban.enum.GACategory
import com.moban.enum.WebViewOption
import com.moban.flow.badges.BadgeDetailActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.BadgeItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.user.Badge
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DateUtil
import com.moban.util.LHPicasso
import com.moban.util.Permission
import com.moban.util.ViewUtil
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class ProfileDetailActivity : BaseMvpActivity<IProfileDetailView, IProfileDetailPresenter>(),
    IProfileDetailView {
    override var presenter: IProfileDetailPresenter = ProfileDetailPresenterIml()

    private var badgeAdapter: BadgeAdapter = BadgeAdapter()
    private var user: User? = null

    companion object {
        const val BUNDLE_KEY_USER = "BUNDLE_KEY_USER"

        fun start(context: Context, user: User) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USER, user)

            val intent = Intent(context, ProfileDetailActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_profile_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        ViewUtil.setMarginTop(profile_detail_tbToolbar, ViewUtil.getStatusBarHeight(getContext()))

        val bundle = intent.extras

        if (intent.hasExtra(BUNDLE_KEY_USER)) {
            user = bundle?.getSerializable(BUNDLE_KEY_USER) as User
            user?.let {
                bindingBasicData(it)

                if (it.isCurrentUser()) {
                    LocalStorage.badges()?.let { badges ->
                        badgeAdapter.badges = badges
                    }

                    if (it.isAnonymous()) {
                        profile_btn_delete_account.visibility = View.GONE
                    }
                } else {
                    presenter.loadBadges(it)
                    profile_btn_delete_account.visibility = View.GONE
                }
            }
        }

        profile_detail_tbToolbar.nav_tvTitle.text = getString(R.string.personal_info)
        profile_detail_tbToolbar.nav_imgBack.setOnClickListener {
            finish()
        }

        initRecycleView()

        user?.let {
            setGAScreenName("Profile Detail ${it.id}", GACategory.ACCOUNT)
        }

        profile_btn_delete_account.setOnClickListener {
            WebViewActivity.start(
                this,
                getString(R.string.request_delete_account),
                "https://ohio.net.vn/policy/delete",
                webOption = WebViewOption.DELETE_ACCOUNT
            )
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        profile_recycler_badge.layoutManager = layoutManager
        profile_recycler_badge.adapter = badgeAdapter

        badgeAdapter.listener = object : BadgeItemHandler {
            override fun onClicked(badge: Badge) {
                BadgeDetailActivity.start(this@ProfileDetailActivity, badge)
            }
        }
    }

    private fun bindingBasicData(user: User) {
        profile_tv_name.text = user.name
        profile_tv_role.text = user.role

        profile_tv_full_name.text = user.name
        profile_tv_gender.text = user.gender
        profile_tv_birthday.text = user.birthday
        profile_tv_email.text = user.email
        profile_tv_phone.text = user.phone
        profile_tv_address.text = user.address

        profile_tv_employee_code.text = user.username
        profile_tv_employee_title.text = user.role
        profile_tv_joined_date.text = DateUtil.dateStringFromSeconds(
            user.entrancedate.toLong(), DateUtil.DF_SIMPLE_STRING
        )

        LHPicasso.loadImage(user.avatar, account_image)

        profile_btn_call.setOnClickListener {
            user.phone?.let {
                Permission.openCalling(this, it)
            }
        }
    }

    override fun bindingBadges(badges: List<Badge>) {
        badgeAdapter.badges = badges
    }
}
