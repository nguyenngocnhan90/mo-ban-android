package com.moban.flow.projects.highlight

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.enum.ProjectHighlightType
import com.moban.flow.projects.ProjectsFragment
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_highlight_project.*
import kotlinx.android.synthetic.main.nav.view.*

/**
 * Created by lenvo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class HighlightProjectActivity : BaseMvpActivity<IHighlightProjectView, IHighlightProjectPresenter>(), IHighlightProjectView {
    override var presenter: IHighlightProjectPresenter = HighlightProjectPresenterIml()
    private var type = ProjectHighlightType.NONE

    override fun getLayoutId(): Int {
        return R.layout.activity_highlight_project
    }

    companion object {
        private const val BUNDLE_KEY_TYPE = "BUNDLE_KEY_TYPE"
        fun start(context: Context, type: ProjectHighlightType) {
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY_TYPE, type.value)

            val intent = Intent(context, HighlightProjectActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Highlight project", GACategory.PROJECT)
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_TYPE)) {
            type = ProjectHighlightType.from(bundle?.getInt(BUNDLE_KEY_TYPE) ?: 0)
        }

        val projectFragment = ProjectsFragment()
        projectFragment.projectType = type

        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.highlight_project_frame_container, projectFragment).commit()

        highlight_project_nav.nav_tvTitle.visibility = View.INVISIBLE
        highlight_project_nav.nav_tvSearchProject.visibility = View.VISIBLE
        highlight_project_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        highlight_project_nav.nav_tvSearchProject.setOnClickListener {
            SearchProjectActivity.start(this)
        }
    }
}
