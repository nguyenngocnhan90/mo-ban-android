package com.moban.flow.badges

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.model.data.user.Badge
import com.moban.mvp.BaseMvpActivity
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.activity_badge_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class BadgeDetailActivity : BaseMvpActivity<IBadgeDetailView, IBadgeDetailPresenter>(), IBadgeDetailView {
    override var presenter: IBadgeDetailPresenter = BadgeDetailPresenterIml()

    companion object {
        const val BUNDLE_KEY_BADGE = "BUNDLE_KEY_BADGE"
        fun start(context: Context, badge: Badge) {
            val intent = Intent(context, BadgeDetailActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_BADGE, badge)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_badge_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        badge_nav.nav_tvTitle.text = getString(R.string.badge_detail_title)
        badge_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_BADGE)) {
            val badge = bundle?.getSerializable(BUNDLE_KEY_BADGE) as Badge
            setGAScreenName("Badge Detail ${badge.badge_Id}", GACategory.ACCOUNT)
            LHPicasso.loadImage(badge.badge_Image, badge_detail_img)
            badge_detail_tv_badge_name.text = badge.badge_Name
            presenter.loadBadgeDetail(badge)
        }
    }

    override fun bindingBadgeDetail(badge: Badge) {
        badge_detail_tv_mission_name.text = badge.mission_Name
        badge_detail_view_mission_title.visibility = if (badge.mission_Name.isEmpty()) View.GONE else View.VISIBLE
    }
}
