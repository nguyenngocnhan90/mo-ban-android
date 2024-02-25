package com.moban.flow.secondary.create.step4

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.StrictMode
import androidx.core.widget.ImageViewCompat
import android.view.View
import android.widget.Toast
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.secondary.AttributeAdapter
import com.moban.adapter.secondary.PhotoHorizontalAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.flow.secondary.create.NewSecondaryData
import com.moban.flow.secondary.create.step3.CreateSecondaryStep3Activity
import com.moban.handler.PhotoHorizontalHandler
import com.moban.model.data.BitmapUpload
import com.moban.model.data.secondary.constant.Attribute
import com.moban.mvp.BaseMvpActivity
import com.moban.util.BitmapUtil
import com.moban.util.DialogUtil
import com.moban.util.Permission
import com.moban.util.Util
import com.moban.view.custom.CustomImageSelectorActivity
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_create_secondary_step4.*
import kotlinx.android.synthetic.main.nav_create_secondary_project.view.*
import kotlinx.android.synthetic.main.tabbar_step_create_secondary.view.*
import java.util.*

class CreateSecondaryStep4Activity : BaseMvpActivity<ICreateSecondaryStep4View, ICreateSecondaryStep4Presenter>(), ICreateSecondaryStep4View {
    override var presenter: ICreateSecondaryStep4Presenter = CreateSecondaryStep4PresenterIml()
    private val imagesAdapter = PhotoHorizontalAdapter()
    private var currentNumImages = 0
    private var isSubmitting = false

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CreateSecondaryStep4Activity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_create_secondary_step4
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setupToolBar()
        initRecycleView()
        secondary_btn_create_new.setOnClickListener {
            checkUploadPhotoComplete()
        }

        secondary_step4_tv_type_secondary.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showAttributeMenu(this, getString(R.string.type_secondary_placeholder), LHApplication.instance.lhCache.getTypeList().attributes,
                    object : AttributeAdapter.AttributeItemHandler {
                        override fun onSelect(item: Attribute) {
                            dialog?.dismiss()
                            NewSecondaryData.type = item
                            secondary_step4_tv_type_secondary.text = item.attribute_Name
                            secondary_step4_tv_type_secondary.setTextColor(Util.getColor(this@CreateSecondaryStep4Activity, R.color.color_black))
                        }
                    })
        }

        loadRawData()
        setGAScreenName("Create Secondary Step 4", GACategory.SECONDARY)
    }

    private fun loadRawData() {
        imagesAdapter.setPhotosList(NewSecondaryData.host_photos)
        NewSecondaryData.type?.let {
            secondary_step4_tv_type_secondary.text = it.attribute_Name
            secondary_step4_tv_type_secondary.setTextColor(Util.getColor(this, R.color.color_black))
        }
    }

    override fun onBackPressed() {
        backToStep3()
    }

    private fun backToStep3() {
        onSaveRawData()
        CreateSecondaryStep3Activity.start(this)
    }

    private fun checkUploadPhotoComplete() {
        if (isSubmitting) {
            new_secondary_progress.visibility = View.VISIBLE
            Runnable {
                while (isSubmitting) {
                    Thread.sleep(500)
                }
                runOnUiThread {
                    submitNewSecondary()
                }
            }
        } else {
            submitNewSecondary()
        }
    }

    private fun submitNewSecondary() {
        onSaveRawData()
        if (NewSecondaryData.host_photos.isEmpty()) {
            DialogUtil.showErrorDialog(this, "Bạn chưa chọn ảnh", "Vui lòng chọn Hình Ảnh về Giấy Tờ Chủ Quyền và Hợp Đồng cho Bất Động Sản",
                    getString(R.string.ok), null)
            return
        }

        if (NewSecondaryData.type == null) {
            DialogUtil.showErrorDialog(this, "Chưa đủ thông tin", "Vui lòng chọn Hình Thức Ký Gửi cho Bất Động Sản",
                    getString(R.string.ok), null)
            return
        }

        var dialog: Dialog? = null
        dialog = DialogUtil.showConfirmDialog(this, false, "Hoàn tất", "Bạn đã nhập đầy đủ thông tin ký gửi Bất Động Sản.\nNhấn 'Hoàn Tất' để tạo ký gửi.",
                "Hoàn Tất", getString(R.string.skip), View.OnClickListener {
            dialog?.dismiss()
            new_secondary_progress.visibility = View.VISIBLE
            presenter.submit(NewSecondaryData.getNewSecondaryParam())
        }, null)
    }

    private fun onSaveRawData() {
        NewSecondaryData.host_photos = imagesAdapter.photos
    }

    private fun initRecycleView() {
        imagesAdapter.enableMainPhoto = false
        val layoutManagerImages = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        secondary_step4_recyclerview_images.layoutManager = layoutManagerImages
        secondary_step4_recyclerview_images.adapter = imagesAdapter
        imagesAdapter.listener = object : PhotoHorizontalHandler {
            override fun onAddPhoto() {
                showImagePicker()
            }

            override fun onDeletePhoto(bitmap: BitmapUpload) {
                imagesAdapter.removePhoto(bitmap)
            }

            override fun onSetMainPhoto(bitmap: BitmapUpload) {
            }
        }
    }

    private fun setupToolBar() {
        create_secondary_step4_nav.nav_img_back.visibility = View.VISIBLE
        create_secondary_step4_nav.nav_img_back.setOnClickListener {
            backToStep3()
        }

        create_secondary_step4_nav.nav_tv_title.text = getString(R.string.confirmation_bds)

        create_secondary_step4_nav.nav_continue.visibility = View.INVISIBLE

        tabbar_steps4.tabbar_view_step4.setBackgroundResource(R.drawable.background_circle_dark)
        ImageViewCompat.setImageTintList(tabbar_steps4.tabbar_img_step4, ColorStateList.valueOf(Util.getColor(this, R.color.color_white)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK &&
                requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            data?.let {
                val images = it.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT)
                        as ArrayList<String>

                val bitmaps: MutableList<BitmapUpload> = ArrayList()
                images.mapTo(bitmaps) {
                    BitmapUpload(Date().time, BitmapUtil.getBitmapFromFile(it))
                }
                currentNumImages += bitmaps.size
                if (imagesAdapter.photos.isEmpty()) {
                    imagesAdapter.setPhotosList(bitmaps)
                } else {
                    imagesAdapter.addPhotosList(bitmaps)
                }

                isSubmitting = true
                presenter.uploadImages(ArrayList(bitmaps))
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Constant.READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImagePicker()
                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun showImagePicker() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(this, CustomImageSelectorActivity::class.java)
        intent.putExtra(ImageSelectorActivity.EXTRA_MAX_SELECT_NUM, Constant.MAX_IMAGE_UPLOAD - currentNumImages)
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_MODE, 1)
        intent.putExtra(ImageSelectorActivity.EXTRA_SHOW_CAMERA, true)
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_PREVIEW, false)
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_CROP, false)

        if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionCamera(this)) {
            startActivityForResult(intent, ImageSelectorActivity.REQUEST_IMAGE)
        }
    }

    override fun showUploadImageFailed(bitmapUpload: BitmapUpload) {
        Toast.makeText(this, getString(R.string.upload_image_error_title), Toast.LENGTH_SHORT).show()
        presenter.removeImage(bitmapUpload)
        imagesAdapter.removePhoto(bitmapUpload)
    }

    override fun showCreateSecondaryFailed(message: String?) {
        new_secondary_progress.visibility = View.GONE
        var body = "Tạo Ký Gửi thất bại. \nVui lòng thử lại sau."
        if (!message.isNullOrEmpty()) {
            body = message
        }
        DialogUtil.showErrorDialog(this, "Đã có lỗi xảy ra", body,
                getString(R.string.ok), null)
    }

    override fun showCreateSecondarySuccess(message: String?) {
        new_secondary_progress.visibility = View.GONE
        var body = "Bạn đã tạo Ký Gửi thành công.\nVui lòng chờ duyệt từ Admin."
        if (!message.isNullOrEmpty()) {
            body = message
        }
        var dialog: Dialog? = null
        dialog = DialogUtil.showInfoDialog(this, "Chúc mừng", body,
                getString(R.string.ok), View.OnClickListener {
            dialog?.dismiss()
            finish()
        })
    }

    override fun uploadComplete() {
        isSubmitting = false
    }
}
