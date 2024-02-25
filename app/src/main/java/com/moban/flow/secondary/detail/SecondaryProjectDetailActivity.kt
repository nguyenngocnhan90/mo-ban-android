package com.moban.flow.secondary.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.adapter.project.PhotoAdapter
import com.moban.enum.GACategory
import com.moban.extension.isValidPhoneNumber
import com.moban.handler.PhotoItemHandler
import com.moban.model.data.Photo
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.model.response.secondary.SecondaryHouseResponse
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.LHPicasso
import com.moban.util.Permission
import com.moban.view.custom.ImageViewerOverlay
import com.moban.view.custom.imageviewer.ImageViewer
import kotlinx.android.synthetic.main.activity_secondary_house_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class SecondaryProjectDetailActivity : BaseMvpActivity<ISecondaryProjectDetailView, ISecondaryProjectDetailPresenter>(), ISecondaryProjectDetailView {
    override var presenter: ISecondaryProjectDetailPresenter = SecondaryProjectDetailPresenterIml()
    private lateinit var house: SecondaryHouse

    private var photoAdapter: PhotoAdapter = PhotoAdapter()
    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        fun start(context: Context, house: SecondaryHouse) {
            val intent = Intent(context, SecondaryProjectDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, house)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_secondary_house_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        secondary_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        secondary_detail_nav.nav_tvTitle.text = getString(R.string.secondary)

        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            return
        }
        initRecycleView()

        house = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as SecondaryHouse
        bindingProjectDetail(house)
        presenter.loadProject(house.id)
        setGAScreenName("Secondary Detail Id = $house.id", GACategory.SECONDARY)
    }

    private fun initRecycleView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        secondary_detail_recycle_view_images.layoutManager = layoutManager
        secondary_detail_recycle_view_images.adapter = photoAdapter
    }

    private fun bindingBasicInfo(house: SecondaryHouse) {
        this.house = house
        secondary_detail_nav.nav_tvTitle.text = house.contract_code
        secondary_detail_tv_title.text = house.contract_code
        secondary_detail_tv_address.text = house.fullAddress()
        secondary_detail_tv_price.text = house.price
        secondary_detail_tv_price.visibility = if (house.price.isEmpty())
            View.INVISIBLE else View.VISIBLE

        secondary_detail_tv_fee.text = house.agent_Money
        secondary_detail_tv_fee.visibility = if (house.agent_Money.isEmpty())
            View.INVISIBLE else View.VISIBLE

        secondary_detail_tv_highlight.visibility = if (house.docquyen.isNullOrEmpty()) View.GONE else View.VISIBLE
        secondary_detail_tv_highlight.text = house.docquyen

        LHPicasso.loadImage(house.image, secondary_detail_img)

        secondary_detail_img.setOnClickListener {
            handleImageClick(house, 0)
        }

        secondary_detail_btn_contact.setOnClickListener {
            contactAdmin()
        }
    }

    private fun contactAdmin() {
        if (house.phone.isValidPhoneNumber()) {
            Permission.openCalling(this, house.phone)
        } else {
            var dialog: Dialog? = null
            dialog = DialogUtil.showConfirmDialog(this, false, "Chốt hàng", "Bạn có chắc chắn muốn chốt hàng?",
                    "Chốt", "Bỏ Qua", View.OnClickListener {
                dialog?.dismiss()
                presenter.requestContact(house.id)
                linkmart_detail_progressbar.visibility = View.VISIBLE
            }, null)
            
        }
    }

    override fun bindingProjectDetail(house: SecondaryHouse) {
        bindingBasicInfo(house)

        secondary_detail_recycle_view_images.visibility = if (house.photos.isNullOrEmpty()) View.GONE else
            View.VISIBLE
        photoAdapter.setPhotosList(house.photos)

        photoAdapter.listener = object: PhotoItemHandler {
            override fun onClicked(photo: Photo, pos: Int) {
                handleImageClick(house, pos + 1)
            }
        }
        secondary_detail_tv_square.text = house.house_Acreage_String()
        secondary_detail_tv_num_of_beds.text = house.house_BedRoom_String()
        secondary_detail_tv_structure.text = house.house_Texture_String()
        secondary_detail_tv_garden.text = house.house_Deck_String()
        secondary_detail_tv_direction.text = house.house_Direction_String()
        secondary_detail_tv_phone.text = house.phone_String()
    }

    private fun handleImageClick(house: SecondaryHouse, index: Int) {
        val photos: MutableList<Photo> = ArrayList()
        val photo = Photo()
        photo.photo_Link = house.image
        photos.add(photo)

        if (!house.photos.isNullOrEmpty()) {
            photos.addAll(house.photos)
        }

        val overlay = ImageViewerOverlay(this)

        val imageViewer = ImageViewer.Builder<Photo>(this, photos)
                .setFormatter { it.photo_Link }
                .setStartPosition(index)
                .setOverlayView(overlay)
                .build()
        imageViewer.show()

        overlay.buttonClose?.setOnClickListener {
            imageViewer.onDismiss()
        }
    }

    override fun bindingRequestContact(response: SecondaryHouseResponse) {
        linkmart_detail_progressbar.visibility = View.GONE
        val isSuccessful = response.success && response.data != null

        if (isSuccessful) {
            val message = response.message ?: ""
            house = response.data!!
            bindingBasicInfo(house)
            bindingProjectDetail(house)
            DialogUtil.showInfoDialog(this, "Yêu cầu  thành công", message, getString(R.string.ok), null)
        } else {
            val error = response.error ?: ""
            bindingRequestContactFailed(error)
        }
    }

    override fun bindingRequestContactFailed(error: String) {
        linkmart_detail_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this, "Yêu cầu không thành công", error, getString(R.string.ok), null)
    }
}
