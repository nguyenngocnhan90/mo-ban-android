package com.moban.flow.secondary.create.simple

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.secondary.ProjectBaseAdapter
import com.moban.flow.secondary.create.NewSecondaryData
import com.moban.model.data.secondary.constant.ProjectBase
import com.moban.model.param.NewSecondaryProjectParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_new_simple_secondary.*
import kotlinx.android.synthetic.main.nav.view.*

class NewSimpleSecondaryActivity : BaseMvpActivity<INewSimpleSecondaryView, INewSimpleSecondaryPresenter>(), INewSimpleSecondaryView {
    override var presenter: INewSimpleSecondaryPresenter = NewSimpleSecondaryPresenterIml()

    override fun getLayoutId(): Int {
        return R.layout.activity_new_simple_secondary
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, NewSimpleSecondaryActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        simple_secondary_nav.nav_imgBack.visibility = View.INVISIBLE
        simple_secondary_nav.nav_imgClose.visibility = View.VISIBLE
        simple_secondary_nav.nav_tvTitle.text = "Tạo Ký Gửi"

        simple_secondary_nav.nav_imgClose.setOnClickListener {
            finish()
        }


        if (!LHApplication.instance.lhCache.isLoadedSecondaryConstant) {
            presenter.getBaseProjects()
        }

        simple_secondary_tv_project.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showProjectMenu(this, LHApplication.instance.lhCache.secondaryBaseProjects,
                    object : ProjectBaseAdapter.ProjectBaseItemHandler {
                        override fun onSelect(item: ProjectBase) {
                            dialog?.dismiss()
                            NewSecondaryData.projectBase = item
                            simple_secondary_tv_project.text = item.product_Name
                            simple_secondary_tv_project.setTextColor(Util.getColor(this@NewSimpleSecondaryActivity, R.color.color_black))
                        }
                    })
        }
        simple_secondary_ed_price.setDecimals(false)
        simple_secondary_ed_fee.setDecimals(false)

        simple_secondary_btn_create_new.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showConfirmDialog(this, false, "Hoàn tất", "Bạn đã nhập đầy đủ thông tin ký gửi Bất Động Sản.\nNhấn 'Hoàn Tất' để tạo ký gửi.",
                    "Hoàn Tất", getString(R.string.skip), View.OnClickListener {
                dialog?.dismiss()
                submitSecondary()
            }, null)
        }
    }

    private fun submitSecondary() {
        val name = simple_secondary_apart_info.text.toString()
        val price = simple_secondary_ed_price.cleanIntValue
        val fee = simple_secondary_ed_fee.cleanIntValue

        if (NewSecondaryData.projectBase == null) {
            DialogUtil.showErrorDialog(this, "Chưa đủ thông tin", "Vui lòng chọn Dự Án cho Bất Động Sản", "OK", null)
            return
        }

        if (name.isEmpty() || price == 0 || fee == 0) {
            DialogUtil.showErrorDialog(this, "Chưa đủ thông tin", "Vui lòng nhập đẩy đủ thông tin cho Bất Động Sản", "OK", null)
            return
        }


        val param = NewSecondaryProjectParam()
        param.house_name = name
        param.product_id = NewSecondaryData.projectBase!!.id
        param.house_price = price
        param.host_rate = fee

        simple_secondary_progress.visibility = View.VISIBLE
        presenter.submit(param)
    }

    override fun showCreateSecondaryFailed(message: String?) {
        simple_secondary_progress.visibility = View.GONE
        var body = "Tạo Ký Gửi thất bại. \nVui lòng thử lại sau."
        if (!message.isNullOrEmpty()) {
            body = message
        }
        DialogUtil.showErrorDialog(this, "Đã có lỗi xảy ra", body,
                getString(R.string.ok), null)
    }

    override fun showCreateSecondarySuccess(message: String?) {
        simple_secondary_progress.visibility = View.GONE
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
}
