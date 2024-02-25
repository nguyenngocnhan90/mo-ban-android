package com.moban.flow.secondary.create.step3

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.StrictMode
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.secondary.CheckboxMicsAdapter
import com.moban.adapter.secondary.PhotoHorizontalAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.flow.secondary.create.NewSecondaryData
import com.moban.flow.secondary.create.step2.CreateSecondaryStep2Activity
import com.moban.flow.secondary.create.step4.CreateSecondaryStep4Activity
import com.moban.handler.PhotoHorizontalHandler
import com.moban.model.data.BitmapUpload
import com.moban.mvp.BaseMvpActivity
import com.moban.util.BitmapUtil
import com.moban.util.DialogUtil
import com.moban.util.Permission
import com.moban.util.Util
import com.moban.view.custom.CustomImageSelectorActivity
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_create_secondary_step3.*
import kotlinx.android.synthetic.main.nav_create_secondary_project.view.*
import kotlinx.android.synthetic.main.tabbar_step_create_secondary.view.*
import java.util.*

class CreateSecondaryStep3Activity : BaseMvpActivity<ICreateSecondaryStep3View, ICreateSecondaryStep3Presenter>(), ICreateSecondaryStep3View {
    override var presenter: ICreateSecondaryStep3Presenter = CreateSecondaryStep3PresenterIml()
    private val micsAdapter = CheckboxMicsAdapter()
    private val utilityAdapter = CheckboxMicsAdapter()
    private val micsImagesAdapter = PhotoHorizontalAdapter()
    private var currentNumImages = 0
    private var isSubmitting = false

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CreateSecondaryStep3Activity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_create_secondary_step3
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setupToolBar()
        initRecycleView()
        loadRawData()
        setGAScreenName("Create Secondary Step 3", GACategory.SECONDARY)
    }

    private fun loadRawData() {
        if (!NewSecondaryData.house_photos.isEmpty()) {
            micsImagesAdapter.setPhotosList(NewSecondaryData.house_photos)
        }
        NewSecondaryData.house_image?.let {
            micsImagesAdapter.mainPhoto = it
        }

        micsAdapter.setDataListSelected(NewSecondaryData.furnitures)
        utilityAdapter.setDataListSelected(NewSecondaryData.utilities)
    }

    override fun onBackPressed() {
        backToStep2()
    }

    private fun backToStep2() {
        onSaveRawData()
        CreateSecondaryStep2Activity.start(this)
    }

    private fun setupToolBar() {
        create_secondary_step3_nav.nav_img_back.visibility = View.VISIBLE
        create_secondary_step3_nav.nav_img_back.setOnClickListener {
            backToStep2()
        }

        create_secondary_step3_nav.nav_tv_title.text = getString(R.string.images_utilities)

        create_secondary_step3_nav.nav_continue.setOnClickListener {
            checkUploadPhotoComplete()
        }

        tabbar_steps3.tabbar_view_step3.setBackgroundResource(R.drawable.background_circle_dark)
        ImageViewCompat.setImageTintList(tabbar_steps3.tabbar_img_step3, ColorStateList.valueOf(Util.getColor(this, R.color.color_white)))
    }

    private fun checkUploadPhotoComplete() {
        if (isSubmitting) {
            step3_progress.visibility = View.VISIBLE
            Runnable {
                while (isSubmitting) {
                    Thread.sleep(500)
                }
                runOnUiThread {
                    showNextStep()
                }
            }
        } else {
            showNextStep()
        }
    }

    private fun showNextStep() {
        onSaveRawData()
        if (NewSecondaryData.house_photos.isEmpty()) {
            DialogUtil.showErrorDialog(this, "Bạn chưa chọn ảnh", "Vui lòng chọn Hình Ảnh để mô tả cho Bất Động Sản",
                    getString(R.string.ok), null)
            return
        }
        CreateSecondaryStep4Activity.start(this@CreateSecondaryStep3Activity)
        finish()
    }

    private fun onSaveRawData() {
        NewSecondaryData.house_photos = micsImagesAdapter.photos
        NewSecondaryData.house_image = micsImagesAdapter.mainPhoto
        NewSecondaryData.furnitures = micsAdapter.getDataListSelected().toMutableList()
        NewSecondaryData.utilities = utilityAdapter.getDataListSelected().toMutableList()
    }

    private fun initRecycleView() {
        val layoutManager = GridLayoutManager(this, 2)
        secondary_step3_recyclerview_mics.layoutManager = layoutManager
        secondary_step3_recyclerview_mics.adapter = micsAdapter
        secondary_step3_recyclerview_mics.isNestedScrollingEnabled = false
        micsAdapter.setDataList(LHApplication.instance.lhCache.getFurnitureList().attributes)

        val layoutManagerUtility = GridLayoutManager(this, 2)
        secondary_step3_recyclerview_utilities.layoutManager = layoutManagerUtility
        secondary_step3_recyclerview_utilities.isNestedScrollingEnabled = false
        secondary_step3_recyclerview_utilities.adapter = utilityAdapter
        utilityAdapter.setDataList(LHApplication.instance.lhCache.getUtilitiesList().attributes)

        val layoutManagerImages = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        secondary_step3_recyclerview_images.layoutManager = layoutManagerImages
        secondary_step3_recyclerview_images.adapter = micsImagesAdapter
        micsImagesAdapter.listener = object: PhotoHorizontalHandler {
            override fun onAddPhoto() {
                showImagePicker()
            }

            override fun onDeletePhoto(bitmap: BitmapUpload) {
                micsImagesAdapter.removePhoto(bitmap)
            }

            override fun onSetMainPhoto(bitmap: BitmapUpload) {
                micsImagesAdapter.updateMainPhoto(bitmap)
            }
        }
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
                if (micsImagesAdapter.photos.isEmpty()) {
                    micsImagesAdapter.setPhotosList(bitmaps)
                } else {
                    micsImagesAdapter.addPhotosList(bitmaps)
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
        micsImagesAdapter.removePhoto(bitmapUpload)
    }

    override fun uploadComplete() {
        isSubmitting = false
    }
}
