package com.moban.flow.linkmart

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.linkmart.LinkmartAdapter
import com.moban.adapter.linkmart.LinkmartRewardAdapter
import com.moban.adapter.linkmart.PurchasedLinkmartAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.enum.LinkmartMainCategory
import com.moban.enum.SearchForType
import com.moban.flow.linkmart.detail.LinkmartDetailActivity
import com.moban.flow.linkmart.list.LinkmartListActivity
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.handler.LinkmartItemHandler
import com.moban.handler.LinkmartRewardItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.deal.Promotion
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_link_mart.*
import kotlinx.android.synthetic.main.linkmart_view_favorite.*
import kotlinx.android.synthetic.main.linkmart_view_main.*
import kotlinx.android.synthetic.main.linkmart_view_main.view.*
import kotlinx.android.synthetic.main.linkmart_view_my_reward.*
import kotlinx.android.synthetic.main.linkmart_view_purchased.*
import kotlinx.android.synthetic.main.nav.view.*

class LinkmartActivity : BaseMvpActivity<ILinkmartView, ILinkmartPresenter>(), ILinkmartView {
    override var presenter: ILinkmartPresenter = LinkmartPresenterIml()
    private lateinit var dealTypeLinkmartPagerView: DealTypeLinkmartPagerView
    private val specialDealAdapter: LinkmartAdapter = LinkmartAdapter(LinkmartAdapter.LINKMART_HORIZONTAL)
    private val newDealAdapter: LinkmartAdapter = LinkmartAdapter(LinkmartAdapter.LINKMART_HORIZONTAL)
    private val popularDealAdapter: LinkmartAdapter = LinkmartAdapter(LinkmartAdapter.LINKMART_HORIZONTAL)

    private val favoriteDealAdapter: LinkmartAdapter = LinkmartAdapter(LinkmartAdapter.LINKMART_VERTICAL)
    private val purchasedAdapter = PurchasedLinkmartAdapter()
    private var pagePurchased = 1
    private var parentId: Int = 0

    private var isShowMyReward = false
    private val rewardAdapter = LinkmartRewardAdapter()
    private var pageReward = 1

    companion object {
        const val BUNDLE_KEY_PARENT_ID = "BUNDLE_KEY_PARENT_ID"

        fun start(context: Context, parentId: Int) {
            val intent = Intent(context, LinkmartActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PARENT_ID, parentId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_link_mart
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Linkmart", GACategory.ACCOUNT)
        linkmart_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        initPagerViewDealType()
        initRecycleView()
        initTabbar()

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PARENT_ID)) {
            parentId = bundle?.getSerializable(BUNDLE_KEY_PARENT_ID) as Int
        }

        val title = when (parentId) {
            LinkmartMainCategory.linkMart.value -> getString(R.string.link_mart)
            LinkmartMainCategory.linkHub.value -> getString(R.string.linkhub)
            LinkmartMainCategory.linkBook.value -> getString(R.string.linkbook)
            LinkmartMainCategory.linkReward.value -> getString(R.string.link_reward)
            else -> getString(R.string.link_mart)
        }
        linkmart_nav.nav_tvTitle.text = title
        isShowMyReward = parentId == LinkmartMainCategory.linkReward.value
        if (isShowMyReward) {
            parentId = LinkmartMainCategory.linkMart.value
        }

        val categories = when (parentId) {
            LinkmartMainCategory.linkMart.value -> LHApplication.instance.lhCache.linkmartCategories
            LinkmartMainCategory.linkHub.value -> LHApplication.instance.lhCache.linkHubCategories
            LinkmartMainCategory.linkBook.value -> LHApplication.instance.lhCache.linkBookCategories
            else -> ArrayList()
        }
        if (categories.isNotEmpty()) {
            bindingCategories(categories)
        }

        linkmart_view_tabbar_purchased.visibility = if (isShowMyReward) View.GONE else View.VISIBLE
        linkmart_view_tabbar_reward.visibility = if (isShowMyReward) View.VISIBLE else View.GONE
        linkmart_view_main.linkmart_special_deal_view.visibility = if (parentId == LinkmartMainCategory.linkBook.value)
            View.GONE else View.VISIBLE

        presenter.loadCategories(parentId, 20)
        presenter.loadNewestDeal(parentId)
        presenter.loadPopularDeal(parentId)
        presenter.loadSpecialDeal(parentId)
//        if (!isShowMyReward) {
//            presenter.loadPurchased(pagePurchased)
//        } else {
            presenter.loadReward(pageReward)
//        }

        presenter.loadProfile()

        linkmart_reward_refresh.setOnRefreshListener {
            pageReward = 0
            presenter.loadReward(pageReward)
        }
        linkmart_reward_refresh.isRefreshing = true

        linkmart_purchased_refresh.setOnRefreshListener {
            pagePurchased = 0
            presenter.loadPurchased(pagePurchased)
        }
        linkmart_purchased_refresh.isRefreshing = true

        linkmart_btn_view_all.setOnClickListener {
            LinkmartListActivity.start(this@LinkmartActivity, parentId)
        }

        bindingCurrentLhc()
    }

    override fun bindingCurrentLhc() {
        LocalStorage.user()?.let {
            val currentLHC = getString(R.string.you_have_N_linkcoin).replace("\$N", it.currentCoinString())
            linkmart_tv_current_lhc.text = currentLHC
        }
    }

    private fun initTabbar() {
        val btnTabs = arrayOf(linkmart_btn_linkmart, linkmart_btn_purchased, linkmart_btn_my_reward)
        btnTabs.forEachIndexed { idx, button ->
            button.setOnClickListener {
                showPageByIndex(idx)
            }
        }
    }

    private fun initPagerViewDealType() {
        dealTypeLinkmartPagerView = DealTypeLinkmartPagerView(this)
        linkmart_view_pager.adapter = dealTypeLinkmartPagerView
    }

    private fun initRecycleView() {
        val arrayRecycleView = arrayOf(linkmart_special_deal_recycleview,
                linkmart_new_deal_recycleview, linkmart_popular_deal_recycleview)
        val arrayAdapter = arrayOf(specialDealAdapter, newDealAdapter, popularDealAdapter)
        arrayRecycleView.forEachIndexed { index, recyclerView ->
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = arrayAdapter[index]
            arrayAdapter[index].listener = object : LinkmartItemHandler {
                override fun onClicked(linkmart: Linkmart) {
                    LinkmartDetailActivity.start(this@LinkmartActivity, linkmart)
                }
            }
        }

        val favoriteLayoutManager = LinearLayoutManager(this)
        linkmart_favorite_recycleview.layoutManager = favoriteLayoutManager
        linkmart_favorite_recycleview.adapter = favoriteDealAdapter

        val purchasedLayoutManager = LinearLayoutManager(this)
        linkmart_purchased_recycleview.layoutManager = purchasedLayoutManager
        linkmart_purchased_recycleview.adapter = purchasedAdapter

        val rewardLayoutManager = LinearLayoutManager(this)
        linkmart_reward_recycleview.layoutManager = rewardLayoutManager
        linkmart_reward_recycleview.adapter = rewardAdapter

        rewardAdapter.listener = object : LinkmartRewardItemHandler {
            override fun onClicked(gift: LevelGift) {
                LinkmartDetailActivity.start(getContext(), gift)
            }

            override fun onReceivedGift(gift: LevelGift) {
                if (gift.isBookingPromotion && gift.using_status == 0) {
                    val promotion = Promotion()
                    promotion.id = gift.id
                    promotion.name = gift.gift_Name

                    SearchProjectActivity.start(this@LinkmartActivity, SearchForType.RESERVATION, promotion)
                    return
                }

                var dialog: Dialog? = null
                dialog = DialogUtil.showConfirmDialog(this@LinkmartActivity, false,
                        getString(R.string.get_gift), getString(R.string.get_gift_confirm),
                        getString(R.string.ok), getString(R.string.skip),
                        View.OnClickListener {
                            dialog?.dismiss()
                            buyGift(gift)
                        }, View.OnClickListener {
                    dialog?.dismiss()
                })
            }
        }
    }

    private fun buyGift(gift: LevelGift) {
        if (gift.linkmart_Id > 0 && gift.gift_level_Id > 0) {
            linkmart_progressbar.visibility = View.VISIBLE
            presenter.markUseGift(gift)
        } else {
            DialogUtil.showErrorDialog(this, getString(R.string.get_gift_not_found_failed_title),
                    getString(R.string.get_gift_failed_desc), getString(R.string.ok), null)
        }
    }

    private fun showPageByIndex(index: Int) {
        val linkmartViewArr = arrayOf(linkmart_view_main, linkmart_view_purchased,
                linkmart_view_my_reward)
        val btnTabs = arrayOf(linkmart_btn_linkmart, linkmart_btn_purchased, linkmart_btn_my_reward)
        val lineTabs = arrayOf(linkmart_line_linkmart, linkmart_line_purchased, linkmart_line_my_reward)

        linkmartViewArr.forEachIndexed { idx, view ->
            view.visibility = if (index == idx) View.VISIBLE else View.GONE
        }

        btnTabs.forEachIndexed { idxSelected, view ->
            val textColor = if (idxSelected == index) R.color.white else R.color.color_white_50
            view.setTextColor(Util.getColor(this@LinkmartActivity, textColor))
        }

        lineTabs.forEachIndexed { idxSelected, view ->
            val visibility = if (idxSelected == index) View.VISIBLE else View.GONE
            view.visibility = visibility
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.DETAIL_LINKMART_REQUEST) {
            data?.let {
//                val order = it.getSerializableExtra(LinkmartDetailActivity.BUNDLE_KEY_LINKMART) as LinkmartOrder
                pagePurchased = 1
                presenter.loadPurchased(pagePurchased)
                presenter.loadProfile()
                showPageByIndex(1)
            }
        }
    }

    override fun bindingCategories(categories: List<LinkmartCategory>) {
        dealTypeLinkmartPagerView.setCategoriesList(categories)
        linkmart_view_pager.forceLayout()
        linkmart_indicator.setupWithViewPager(linkmart_view_pager, true)
    }

    override fun bindingSpecials(linkmarts: List<Linkmart>) {
        specialDealAdapter.setListLinkmartsAndRefresh(linkmarts)
    }

    override fun bindingPopular(linkmarts: List<Linkmart>) {
        popularDealAdapter.setListLinkmartsAndRefresh(linkmarts)
    }

    override fun bindingNewest(linkmarts: List<Linkmart>) {
        newDealAdapter.setListLinkmartsAndRefresh(linkmarts)
    }

    override fun bindingOrders(orders: List<LinkmartOrder>) {
        purchasedAdapter.setListPurchased(orders)
        linkmart_purchased_refresh.isRefreshing = false
    }

    override fun bindingRewards(gifts: List<LevelGift>) {
        rewardAdapter.setGiftList(gifts)
        linkmart_reward_refresh.isRefreshing = false
    }

    override fun purchaseFailed(error: String?) {
        linkmart_progressbar.visibility = View.GONE

        val message = error ?: getString(R.string.get_gift_failed_desc)
        DialogUtil.showErrorDialog(this, getString(R.string.get_gift_failed_title),
                message, getString(R.string.ok), null)
    }

    override fun markUseCompleted(gifts: List<LevelGift>, gift: LevelGift) {
        if (gifts.isNotEmpty()) {
            bindingRewards(gifts)
        }

        gift.using_status = 1
        rewardAdapter.updateGift(gift)

        presenter.buyLinkmart(gift)
    }

    override fun purchaseCompleted() {
        linkmart_progressbar.visibility = View.GONE
        DialogUtil.showInfoDialog(this, getString(R.string.get_gift_congrats_title),
                getString(R.string.get_gift_congrats_desc), getString(R.string.ok), null)
    }
}
