package com.moban.flow.linkmart.detail

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import com.moban.LHApplication
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.enum.SearchForType
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.helper.LocalStorage
import com.moban.model.data.deal.Promotion
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.LHPicasso
import com.moban.util.StringUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_linkmart_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class LinkmartDetailActivity : BaseMvpActivity<ILinkmartDetailView, ILinkmartPresenter>(), ILinkmartDetailView {
    override var presenter: ILinkmartPresenter = LinkmartPresenterIml()
    private lateinit var linkmart: Linkmart
    private var linkmartId: Int = 0
    private var levelGift: LevelGift? = null
    private var giftId: Int = 0
    private var dialog: Dialog? = null

    companion object {
        val BUNDLE_KEY_LINKMART = "BUNDLE_KEY_LINKMART"
        val BUNDLE_KEY_LINKMART_ID = "BUNDLE_KEY_LINKMART_ID"
        val BUNDLE_KEY_GIFT = "BUNDLE_KEY_GIFT"
        val BUNDLE_KEY_GIFT_ID = "BUNDLE_KEY_GIFT_ID"

        fun start(context: Context, linkmart: Linkmart) {
            val intent = Intent(context, LinkmartDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_LINKMART, linkmart)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, linkmartId: Int) {
            val intent = Intent(context, LinkmartDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_LINKMART_ID, linkmartId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun startGift(context: Context, giftId: Int) {
            val intent = Intent(context, LinkmartDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_GIFT_ID, giftId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(activity: Activity, linkmart: Linkmart) {
            val intent = Intent(activity, LinkmartDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_LINKMART, linkmart)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.DETAIL_LINKMART_REQUEST)
        }

        fun start(context: Context, gift: LevelGift) {
            val intent = Intent(context, LinkmartDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_GIFT, gift)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_linkmart_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        linkmart_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        if (intent.hasExtra(BUNDLE_KEY_LINKMART)) {
            linkmart = bundle?.getSerializable(BUNDLE_KEY_LINKMART) as Linkmart
            bindLinkmartInfo()
        }

        if (intent.hasExtra(BUNDLE_KEY_LINKMART_ID)) {
            linkmartId = bundle?.getSerializable(BUNDLE_KEY_LINKMART_ID) as Int
            linkmart_detail_progressbar.visibility = View.VISIBLE
            presenter.loadLinkmartDetail(linkmartId)
        }

        if (intent.hasExtra(BUNDLE_KEY_GIFT_ID)) {
            giftId = bundle?.getSerializable(BUNDLE_KEY_GIFT_ID) as Int
            linkmart_detail_progressbar.visibility = View.VISIBLE
            presenter.getLevelGift(giftId)
        }

        if (intent.hasExtra(BUNDLE_KEY_GIFT)) {
            levelGift = bundle?.getSerializable(BUNDLE_KEY_GIFT) as LevelGift
            bindGiftInfo()
        }

        setGAScreenName("Linkmart Detail $linkmartId", GACategory.ACCOUNT)
        linkmart_detail_btn_buy_it.setOnClickListener {
            if (levelGift != null) {
                askToReceivedGift(levelGift!!)
            } else {
                purchaseLinkmart()
            }
        }
    }

    private fun purchaseLinkmart() {
        if (!::linkmart.isInitialized) {
            return
        }
        LocalStorage.user()?.let {
            if (linkmart.price.toIntOrNull() != null && it.current_Coin < linkmart.price.toInt()) {
                DialogUtil.showErrorDialog(this, getString(R.string.purchase_failed_no_enough_title),
                        getString(R.string.purchase_failed_no_enough_desc), getString(R.string.ok), null)
                return
            }
        }

        linkmart_detail_progressbar.visibility = View.VISIBLE
        linkmart_detail_btn_buy_it.isEnabled = false
        presenter.purchase(linkmart)
    }

    private fun askToReceivedGift(gift: LevelGift) {
        if (gift.isBookingPromotion && gift.using_status == 0) {
            val promotion = Promotion()
            promotion.id = gift.id
            promotion.name = gift.gift_Name

            SearchProjectActivity.start(this, SearchForType.RESERVATION, promotion)
            return
        }

        var dialog: Dialog? = null
        dialog = DialogUtil.showConfirmDialog(this, false,
                getString(R.string.get_gift), getString(R.string.get_gift_confirm),
                getString(R.string.ok), getString(R.string.skip),
                View.OnClickListener {
                    dialog?.dismiss()
                    buyGift(gift)
                }, View.OnClickListener {
            dialog?.dismiss()
        })
    }

    private fun buyGift(gift: LevelGift) {
        val linkmartId = gift.linkmart_Id
        if (linkmartId != 0) {
            linkmart_detail_progressbar.visibility = View.VISIBLE
            presenter.markUseGift(gift)
        } else if (linkmartId == 0) {
            DialogUtil.showErrorDialog(this, getString(R.string.get_gift_not_found_failed_title),
                    getString(R.string.get_gift_failed_desc), getString(R.string.ok), null)
        }
    }

    override fun bindingLevelGift(levelGift: LevelGift) {
        linkmart_detail_progressbar.visibility = View.GONE
        this.levelGift = levelGift
        bindGiftInfo()
    }

    override fun loadLevelGiftFailed() {
        linkmart_detail_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this, getString(R.string.get_gift_not_found_failed_title),
                getString(R.string.get_gift_failed_desc), getString(R.string.ok), null)
    }

    private fun bindGiftInfo() {
        levelGift?.let {
            val isUsed = it.using_status != 0
            linkmart_detail_btn_buy_it.text = if (isUsed) getText(R.string.received) else getText(R.string.get_it)
            linkmart_detail_btn_buy_it.isEnabled = !isUsed
            linkmart_detail_btn_buy_it.setBackgroundColor(Util.getColor(this, if (isUsed) R.color.colorNotification else R.color.color_porcorn))

            val name = it.gift_Name
            linkmart_detail_nav.nav_tvTitle.text = name
            linkmart_detail_tv_title.text = name
            linkmart_detail_tv_price.text = ""
            if (!it.gift_Body.isNullOrBlank()) {
                linkmart_detail_tv_description.text = StringUtil.fromHtml(it.gift_Body)
            }

            LHPicasso.loadImage(it.gift_Image, linkmart_detail_img_gift)
        }
    }

    private fun bindLinkmartInfo() {
        var depositTitle = ""

        linkmart.categories?.let { categories ->
            val linkbookCate = categories.firstOrNull { it.id == Constant.LINKBOOK_CATEGORY_ID }
            if (linkbookCate != null) {
                depositTitle = getString(R.string.book_deposit)
                linkmart_detail_btn_buy_it.text = getText(R.string.borrow_book)
            }
        }

        linkmart_detail_nav.nav_tvTitle.text = linkmart.name
        linkmart_detail_tv_title.text = linkmart.name

        val hasSalePrice = linkmart.salePrice != null && linkmart.salePrice!!.isNotEmpty()

        val regularPrice = (depositTitle + " " + linkmart.regularPrice).trim() +
                " " + getString(R.string.lhc) +
                if (hasSalePrice) " - " else ""
        val spannablePrice = SpannableString(regularPrice)
        if (hasSalePrice) {
            spannablePrice.setSpan(StrikethroughSpan(), 0, regularPrice.length - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val salePrice = linkmart.salePrice + " " + getString(R.string.lhc)
            linkmart_detail_tv_coin.text = salePrice
        }
        linkmart_detail_tv_price.text = spannablePrice

        linkmart_detail_tv_description.text = StringUtil.fromHtml(linkmart.description)

        linkmart.images?.firstOrNull()?.let {
            LHPicasso.loadImage(it.url, linkmart_detail_img_gift)
        }

        //Brand
        linkmart.brands?.firstOrNull()?.let {
            linkmart_detail_tv_brand.text = it.name

            val brand = LHApplication.instance.lhCache.brands.firstOrNull { brand -> brand.id == it.id }
            brand?.let {
                it.image?.let {
                    LHPicasso.loadImage(it.url, linkmart_detail_img_brand)
                }
            }
        }

    }

    override fun bindingProductDetail(linkmart: Linkmart) {
        linkmart_detail_progressbar.visibility = View.GONE
        this.linkmart = linkmart
        bindLinkmartInfo()
    }

    override fun bindingProductPurchased(linkmartOrder: LinkmartOrder) {
        linkmart_detail_progressbar.visibility = View.GONE
        linkmart_detail_btn_buy_it.isEnabled = true
        val intent = Intent(this, LinkmartDetailActivity::class.java)
        intent.putExtra(BUNDLE_KEY_LINKMART, linkmartOrder)
        setResult(Constant.DETAIL_LINKMART_REQUEST, intent)

        dialog = DialogUtil.showInfoDialog(this, getString(R.string.purchase_successfully_title),
                getString(R.string.purchase_successfully_desc), getString(R.string.ok),
                View.OnClickListener {
                    backToHome()
                })
    }

    private fun backToHome() {
        dialog?.dismiss()
        finish()
    }

    override fun bindingPurchaseFailed(error: String?) {
        linkmart_detail_progressbar.visibility = View.GONE
        val message = if (error.isNullOrEmpty()) getString(R.string.purchase_failed_desc) else error

        DialogUtil.showErrorDialog(this, getString(R.string.purchase_failed_title),
                message, getString(R.string.ok), null)
        linkmart_detail_btn_buy_it.isEnabled = true
    }

    override fun purchaseFailed(error: String?) {
        linkmart_detail_progressbar.visibility = View.GONE

        val message = error ?: getString(R.string.get_gift_failed_desc)
        DialogUtil.showErrorDialog(this, getString(R.string.get_gift_failed_title),
                message, getString(R.string.ok), null)
    }

    override fun markUseCompleted(gifts: List<LevelGift>, gift: LevelGift) {
        if (gift.linkmart_Id > 0) {
            presenter.buyGiftLinkmart(gift)
        }
    }

    override fun purchaseCompleted() {
        linkmart_detail_progressbar.visibility = View.GONE
        DialogUtil.showInfoDialog(this, getString(R.string.get_gift_congrats_title),
                getString(R.string.get_gift_congrats_desc), getString(R.string.ok), null)
    }
}
