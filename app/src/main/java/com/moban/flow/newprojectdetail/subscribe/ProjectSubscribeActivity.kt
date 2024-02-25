package com.moban.flow.newprojectdetail.subscribe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.PackageAdapter
import com.moban.handler.PackageItemHandler
import com.moban.model.data.project.Project
import com.moban.model.data.project.SalePackage
import com.moban.mvp.BaseMvpActivity
import com.moban.util.StringUtil
import kotlinx.android.synthetic.main.activity_project_subscribe.*
import kotlinx.android.synthetic.main.nav.view.*

class ProjectSubscribeActivity : BaseMvpActivity<IProjectSubscribeView, IProjectSubscribePresenter>(), IProjectSubscribeView {
    override var presenter: IProjectSubscribePresenter = ProjectSubscribePresenterIml()
    private lateinit var project: Project
    private val packageAdapter = PackageAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_project_subscribe
    }

    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(context: Context, project: Project) {
            val intent = Intent(context, ProjectSubscribeActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        project_sub_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        project_sub_nav.nav_tvTitle.text = "Đăng Ký Bán"

        initRecycleView()
        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            return
        }

        project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        presenter.loadPackage(project.id)
    }

    private fun initRecycleView() {
        val layoutManager = org.solovyev.android.views.llm.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        project_sub_package_recycle_view.layoutManager = layoutManager
        project_sub_package_recycle_view.adapter = packageAdapter
        packageAdapter.listener = object: PackageItemHandler {
            override fun onViewDetail(salePackage: SalePackage) {
                bindingNote(salePackage)
            }

            override fun onSubscribed(salePackage: SalePackage) {
                presenter.joinPackage(project.id, salePackage.code)
            }
        }
    }

    override fun bindingPackages(packages: List<SalePackage>) {
        packageAdapter.updateBlocks(packages)
        val salePackage = packages.firstOrNull()
        if (salePackage != null) {
            bindingNote(salePackage)
        }
    }

    private fun bindingNote(salePackage: SalePackage) {
        val htmlString = "<span style=\"font-family: Lato; font-style: normal; font-variant: normal; font-size: 14px; color:#00325D\">" + salePackage.subscriber_Description + "</span>"
        project_sub_tv_note.text = StringUtil.fromHtml(htmlString)
    }
}
