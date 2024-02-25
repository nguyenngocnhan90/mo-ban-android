package com.moban.flow.newprojectdetail.salekit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.DocumentGroupAdapter
import com.moban.model.data.document.DocumentGroup
import com.moban.model.data.project.Project
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_sale_kit.*
import kotlinx.android.synthetic.main.nav.view.*

class SaleKitActivity : BaseMvpActivity<ISaleKitView, ISaleKitPresenter>(), ISaleKitView {
    override var presenter: ISaleKitPresenter = SaleKitPresenterIml()
    private var documentSaleKitsGroupAdapter: DocumentGroupAdapter = DocumentGroupAdapter()
    private lateinit var project: Project
    companion object {
        private const val POLICY_PAGE_INDEX = 3
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(context: Context, project: Project) {
            val intent = Intent(context, SaleKitActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_sale_kit
    }

    override fun initialize(savedInstanceState: Bundle?) {
        salekit_tbToolbar.nav_tvTitle.text = "Sale Kits"
        salekit_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
            salekit_refresh_layout.isRefreshing = true
            presenter.loadSaleKits(project.id)
        }
        initRecycleView()

        salekit_refresh_layout.setOnRefreshListener {
            salekit_refresh_layout.isRefreshing = true
            presenter.loadSaleKits(project.id)
        }
    }

    private fun initRecycleView() {
        val linearLayoutManager = LinearLayoutManager(this)
        salekit_recycler_view.layoutManager = linearLayoutManager
        salekit_recycler_view.adapter = documentSaleKitsGroupAdapter
    }

    override fun bindingSaleKitsProject(documents: List<DocumentGroup>) {
        salekit_refresh_layout.isRefreshing = false
        documentSaleKitsGroupAdapter.setDocumentGroupList(documents)
    }
}
