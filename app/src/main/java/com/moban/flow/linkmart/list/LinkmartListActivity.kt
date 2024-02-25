package com.moban.flow.linkmart.list

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.linkmart.LinkmartAdapter
import com.moban.enum.GACategory
import com.moban.enum.LinkmartMainCategory
import com.moban.enum.LinkmartSortType
import com.moban.flow.linkmart.detail.LinkmartDetailActivity
import com.moban.handler.LinkmartCateHandler
import com.moban.handler.LinkmartItemHandler
import com.moban.handler.LinkmartSortMenuHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogBottomSheetUtil
import kotlinx.android.synthetic.main.activity_linkmart_list.*
import kotlinx.android.synthetic.main.nav.view.*

class LinkmartListActivity : BaseMvpActivity<ILinkmartListView, ILinkmartListPresenter>(), ILinkmartListView {
    override var presenter: ILinkmartListPresenter = LinkmartListPresenterIml()
    private val listDealAdapter: LinkmartAdapter = LinkmartAdapter(LinkmartAdapter.LINKMART_VERTICAL)
    private var cateId: Int? = null
    private var orderBy = LinkmartSortType.NEWEST
    private var page = 1
    private var dialog: Dialog? = null
    private lateinit var curCate: LinkmartCategory

    companion object {
        private val BUNDLE_KEY_LINKMART_CATEGORY = "BUNDLE_KEY_LINKMART_CATEGORY"
        private val BUNDLE_KEY_LINKMART_CATEGORY_ID = "BUNDLE_KEY_LINKMART_CATEGORY_ID"
        fun start(context: Context) {
            val intent = Intent(context, LinkmartListActivity::class.java)
            context.startActivity(intent)
        }

        fun start(context: Context, cate: LinkmartCategory) {
            val intent = Intent(context, LinkmartListActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_LINKMART_CATEGORY, cate)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, cateId: Int) {
            val intent = Intent(context, LinkmartListActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_LINKMART_CATEGORY_ID, cateId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_linkmart_list
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("List Linkmart", GACategory.ACCOUNT)
        val bundle = intent.extras
        linkmart_list_nav.nav_tvTitle.text = getText(R.string.link_reward)
        linkmart_list_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        initRecycleView()

        if (intent.hasExtra(BUNDLE_KEY_LINKMART_CATEGORY)) {
            val cate = bundle?.getSerializable(BUNDLE_KEY_LINKMART_CATEGORY) as LinkmartCategory
            cateId = cate.id
            curCate = cate
            linkmart_list_nav.nav_tvTitle.text = cate.name
            linkmart_list_view_sort_cate.visibility = View.GONE
            reloadData()
        } else if (intent.hasExtra(BUNDLE_KEY_LINKMART_CATEGORY_ID)) {
            cateId = bundle?.getSerializable(BUNDLE_KEY_LINKMART_CATEGORY_ID) as Int
            linkmart_list_nav.nav_tvTitle.text = getString(R.string.link_reward)
            linkmart_list_view_sort_cate.visibility = View.GONE
            presenter.getCategory(cateId!!)
        } else {
            reloadData()
        }

        initFilterSearch()
    }

    private fun initFilterSearch() {
        linkmart_view_sort_type.setOnClickListener {
            dialog = DialogBottomSheetUtil.showDialogSortLinkmartMenu(this, orderBy, object : LinkmartSortMenuHandler {
                override fun onClickSort(sortType: LinkmartSortType) {
                    orderBy = sortType
                    filterSort()
                }
            })
        }

        val allCategory = LinkmartCategory()
        allCategory.id = -1
        allCategory.name = getString(R.string.all)
        curCate = allCategory

        val categories: MutableList<LinkmartCategory> = ArrayList()
        categories.add(allCategory)

        val categoriesList = when (cateId) {
            LinkmartMainCategory.linkMart.value -> LHApplication.instance.lhCache.linkmartCategories
            LinkmartMainCategory.linkBook.value -> LHApplication.instance.lhCache.linkBookCategories
            LinkmartMainCategory.linkHub.value -> LHApplication.instance.lhCache.linkHubCategories
            else -> ArrayList()
        }
        categories.addAll(categoriesList)

        linkmart_list_view_sort_cate.setOnClickListener {
            dialog = DialogBottomSheetUtil.showDialogLinkmartCategoryMenu(this, curCate,
                    categories, object : LinkmartCateHandler {
                override fun onClicked(cate: LinkmartCategory) {
                    curCate = cate
                    linkmart_list_nav.nav_tvTitle.text = cate.name
                    cateId = if (cate.id == -1) null else cate.id
                    filterCate()
                }
            })
        }
    }

    private fun filterCate() {
        linkmart_list_tv_sort_cate.text = curCate.name
        dialog?.dismiss()
        reloadData(true)
    }

    private fun filterSort() {
        linkmart_list_tv_sort.text = orderBy.toString(this)
        dialog?.dismiss()
        reloadData(true)
    }

    private fun reloadData(forceClean: Boolean = false) {
        page = 1
        linkmart_list_refresh_products.isRefreshing = true
        if (forceClean) {
            listDealAdapter.clearLinkmarts()
        }
        loadData()
    }

    private fun loadData() {
        when (orderBy) {
            LinkmartSortType.FEATURE -> {
                presenter.loadPopularDeal(page, cateId)
            }
            LinkmartSortType.SALE -> {
                presenter.loadSpecialDeal(page, cateId)
            }
            LinkmartSortType.NEWEST -> {
                presenter.loadNewestDeal(page, cateId)
            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        linkmart_list_recycleview.layoutManager = layoutManager
        linkmart_list_recycleview.adapter = listDealAdapter
        listDealAdapter.listener = object : LinkmartItemHandler {
            override fun onClicked(linkmart: Linkmart) {
                LinkmartDetailActivity.start(this@LinkmartListActivity, linkmart)
            }
        }

        listDealAdapter.loadMoreHandler = object: LoadMoreHandler {
            override fun onLoadMore() {
                loadData()
            }
        }

        linkmart_list_refresh_products.setOnRefreshListener {
            reloadData()
        }
        linkmart_list_refresh_products.isRefreshing = true
    }

    override fun bindingAllProducts(products: List<Linkmart>) {
        if (page == 1) {
            listDealAdapter.setListLinkmarts(products)
        } else {
            listDealAdapter.appendLinkmarts(products)
        }

        val isLoadmore = if (curCate.id != -1) {
            val count = listDealAdapter.linkmarts.size
            count < curCate.count
        } else {
            products.isNotEmpty()
        }
        listDealAdapter.setIsLoadMore(isLoadmore)

        page += if (isLoadmore) 1 else 0
        linkmart_list_refresh_products.isRefreshing = false
    }

    override fun bindingCategory(cate: LinkmartCategory) {
        curCate = cate
        cateId = cate.id
        linkmart_list_nav.nav_tvTitle.text = cate.name
        reloadData()
    }
}
