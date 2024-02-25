package com.moban.flow.secondary.create.step2

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.widget.ImageViewCompat
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.secondary.BasicConstantAdapter
import com.moban.adapter.secondary.HouseTypeAdapter
import com.moban.adapter.secondary.ProjectBaseAdapter
import com.moban.enum.GACategory
import com.moban.extension.toFormatString
import com.moban.flow.secondary.create.NewSecondaryData
import com.moban.flow.secondary.create.simple.NewSimpleSecondaryActivity
import com.moban.flow.secondary.create.step3.CreateSecondaryStep3Activity
import com.moban.model.data.secondary.constant.BasicConstantType
import com.moban.model.data.secondary.constant.HouseType
import com.moban.model.data.secondary.constant.ProjectBase
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_create_secondary_step2.*
import kotlinx.android.synthetic.main.nav_create_secondary_project.view.*
import kotlinx.android.synthetic.main.tabbar_step_create_secondary.view.*

class CreateSecondaryStep2Activity : BaseMvpActivity<ICreateSecondaryStep2View, ICreateSecondaryStep2Presenter>(), ICreateSecondaryStep2View {
    override var presenter: ICreateSecondaryStep2Presenter = CreateSecondaryStep2PresenterIml()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CreateSecondaryStep2Activity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_create_secondary_step2
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setupToolBar()
        setupMenuDialog()
        setGAScreenName("Create Secondary Step 2", GACategory.SECONDARY)

        loadRawData()
    }

    override fun onBackPressed() {
        backToStep1()
    }

    private fun backToStep1() {
        onSaveRawData()
        NewSimpleSecondaryActivity.start(this)
    }

    private fun setupMenuDialog() {
        //Project base
        secondary_step2_ed_project_code_name.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showProjectMenu(this, LHApplication.instance.lhCache.secondaryBaseProjects,
                    object : ProjectBaseAdapter.ProjectBaseItemHandler {
                        override fun onSelect(item: ProjectBase) {
                            dialog?.dismiss()
                            NewSecondaryData.projectBase = item
                            secondary_step2_ed_project_code_name.text = item.product_Name
                            secondary_step2_ed_project_code_name.setTextColor(Util.getColor(this@CreateSecondaryStep2Activity, R.color.color_black))
                        }
                    })
        }

        //Type BDS
        secondary_step1_view_select_type_bds.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showHouseTypeMenu(this, LHApplication.instance.lhCache.houseTypes,
                    object : HouseTypeAdapter.HouseTypeItemHandler {
                        override fun onSelect(item: HouseType) {
                            dialog?.dismiss()
                            NewSecondaryData.houseType = item
                            secondary_step2_tv_select_type.text = item.house_Type_Name
                            secondary_step2_tv_select_type.setTextColor(Util.getColor(this@CreateSecondaryStep2Activity, R.color.color_black))
                        }
                    })
        }

        //Purpose BDS
        secondary_step2_view_select_purpose.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showBasicConstantMenu(this, getString(R.string.select_purpose_title), LHApplication.instance.lhCache.targetHouseTypes,
                    object : BasicConstantAdapter.BasicConstantTypeItemHandler {
                        override fun onSelect(item: BasicConstantType) {
                            dialog?.dismiss()
                            NewSecondaryData.targetHouse = item
                            secondary_step2_tv_select_purpose.text = item.name
                            secondary_step2_tv_select_purpose.setTextColor(Util.getColor(this@CreateSecondaryStep2Activity, R.color.color_black))
                        }
                    })
        }

        //Units
        if (NewSecondaryData.priceUnit == null) {
            NewSecondaryData.priceUnit = LHApplication.instance.lhCache.priceUnits.first()
        }

        if (NewSecondaryData.feeUnit == null) {
            NewSecondaryData.feeUnit = LHApplication.instance.lhCache.agentPriceUnits.first()
        }

        NewSecondaryData.priceUnit?.let {
            secondary_step2_tv_price_bds_unit.text = it.name
        }
        secondary_step2_tv_price_bds_unit.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showBasicConstantMenu(this, getString(R.string.select_unit_price_title), LHApplication.instance.lhCache.priceUnits,
                    object : BasicConstantAdapter.BasicConstantTypeItemHandler {
                        override fun onSelect(item: BasicConstantType) {
                            dialog?.dismiss()
                            NewSecondaryData.priceUnit = item
                            secondary_step2_tv_price_bds_unit.text = item.name
                            secondary_step2_tv_price_bds_unit.setTextColor(Util.getColor(this@CreateSecondaryStep2Activity, R.color.color_black))
                        }
                    })
        }

        //Fee
        NewSecondaryData.feeUnit?.let {
            secondary_step2_tv_fee_owner_unit.text = it.name
        }
        secondary_step2_tv_fee_owner_unit.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showBasicConstantMenu(this, getString(R.string.select_unit_price_title), LHApplication.instance.lhCache.agentPriceUnits,
                    object : BasicConstantAdapter.BasicConstantTypeItemHandler {
                        override fun onSelect(item: BasicConstantType) {
                            dialog?.dismiss()
                            NewSecondaryData.feeUnit = item
                            secondary_step2_tv_fee_owner_unit.text = item.name
                            secondary_step2_tv_fee_owner_unit.setTextColor(Util.getColor(this@CreateSecondaryStep2Activity, R.color.color_black))
                        }
                    })
        }

        //Direction
        secondary_step1_view_select_direction.setOnClickListener {
            var dialog: Dialog? = null
            dialog = DialogUtil.showBasicConstantMenu(this, getString(R.string.select_direction_title), LHApplication.instance.lhCache.directions,
                    object : BasicConstantAdapter.BasicConstantTypeItemHandler {
                        override fun onSelect(item: BasicConstantType) {
                            dialog?.dismiss()
                            NewSecondaryData.direction = item
                            secondary_step2_tv_select_direction.text = item.name
                            secondary_step2_tv_select_direction.setTextColor(Util.getColor(this@CreateSecondaryStep2Activity, R.color.color_black))
                        }
                    })
        }
    }

    private fun loadRawData() {
        secondary_step2_ed_short_title.setText(NewSecondaryData.name)

        val colorDark = Util.getColor(this, R.color.color_black)
        NewSecondaryData.area?.let {
            secondary_step2_ed_total_area.setText(it.toFormatString())
        }

        NewSecondaryData.targetHouse?.let {
            secondary_step2_tv_select_purpose.text = it.name
            secondary_step2_tv_select_purpose.setTextColor(colorDark)
        }

        NewSecondaryData.houseType?.let {
            secondary_step2_tv_select_type.text = it.house_Type_Name
            secondary_step2_tv_select_type.setTextColor(colorDark)
        }

        NewSecondaryData.direction?.let {
            secondary_step2_tv_select_direction.text = it.name
            secondary_step2_tv_select_direction.setTextColor(colorDark)
        }

        NewSecondaryData.projectBase?.let {
            secondary_step2_ed_project_code_name.text = it.product_Name
            secondary_step2_ed_project_code_name.setTextColor(colorDark)
        }

        NewSecondaryData.feeUnit?.let {
            secondary_step2_tv_fee_owner_unit.text = it.name
        }

        NewSecondaryData.fee?.let {
            secondary_step2_ed_fee_owner.setText(it.toString())
        }

        NewSecondaryData.priceUnit?.let {
            secondary_step2_tv_price_bds_unit.text = it.name
        }
        NewSecondaryData.price?.let {
            secondary_step2_ed_price_bds.setText(it.toString())
        }

        NewSecondaryData.agentFee?.let {
            secondary_step2_ed_fee_bds.setText(it.toString())
        }

        secondary_step2_ed_texture.setText(NewSecondaryData.house_texture)

        NewSecondaryData.house_rear_hatch?.let {
            secondary_step2_ed_no_hau.setText(it.toFormatString())
        }

        NewSecondaryData.house_acreage?.let {
            secondary_step2_ed_place_area.setText(it.toFormatString())
        }

        NewSecondaryData.house_acreage_build?.let {
            secondary_step2_ed_construction_area.setText(it.toFormatString())
        }

        NewSecondaryData.house_deck?.let {
            secondary_step2_ed_street_front_house.setText(it.toFormatString())
        }

        NewSecondaryData.house_bedroom?.let {
            secondary_step2_ed_num_of_beds.setText(it.toString())
        }

        NewSecondaryData.house_wc?.let {
            secondary_step2_ed_num_wc.setText(it.toString())
        }

        secondary_step2_ed_description.setText(NewSecondaryData.house_description)


    }

    private fun setupToolBar() {
        create_secondary_step2_nav.nav_img_back.visibility = View.VISIBLE
        create_secondary_step2_nav.nav_img_back.setOnClickListener {
            backToStep1()
        }

        create_secondary_step2_nav.nav_tv_title.text = getString(R.string.info_bds_title)

        create_secondary_step2_nav.nav_continue.setOnClickListener {
            showNextStep()
        }

        tabbar_steps2.tabbar_view_step2.setBackgroundResource(R.drawable.background_circle_dark)
        ImageViewCompat.setImageTintList(tabbar_steps2.tabbar_img_step2, ColorStateList.valueOf(Util.getColor(this, R.color.color_white)))

        secondary_step2_view_advance_info.setOnClickListener {
            val isShow = secondary_step2_content_advance_info.visibility == View.GONE
            secondary_step2_content_advance_info.visibility = if (isShow) View.VISIBLE else View.GONE
            secondary_step2_img_advance_info.setImageResource(if (isShow) R.drawable.arrow_up else R.drawable.arrow_down)
        }
    }

    private fun onSaveRawData() {
        NewSecondaryData.name = secondary_step2_ed_short_title.text.toString()
        NewSecondaryData.area = secondary_step2_ed_total_area.text.toString().toDoubleOrNull()
        NewSecondaryData.price = secondary_step2_ed_price_bds.text.toString().toIntOrNull()
        NewSecondaryData.fee = secondary_step2_ed_fee_owner.text.toString().toIntOrNull()
        NewSecondaryData.agentFee = secondary_step2_ed_fee_bds.text.toString().toIntOrNull()
        NewSecondaryData.house_texture = secondary_step2_ed_texture.text.toString()
        NewSecondaryData.house_rear_hatch = secondary_step2_ed_no_hau.text.toString().toDoubleOrNull()
        NewSecondaryData.house_acreage = secondary_step2_ed_place_area.text.toString().toDoubleOrNull()
        NewSecondaryData.house_acreage_build = secondary_step2_ed_construction_area.text.toString().toDoubleOrNull()
        NewSecondaryData.house_deck = secondary_step2_ed_street_front_house.text.toString().toDoubleOrNull()
        NewSecondaryData.house_bedroom = secondary_step2_ed_num_of_beds.text.toString().toIntOrNull()
        NewSecondaryData.house_wc = secondary_step2_ed_num_wc.text.toString().toIntOrNull()
        NewSecondaryData.house_description = secondary_step2_ed_description.text.toString()
    }

    private fun showNextStep() {
        onSaveRawData()
        if (!isValidRequireData()) {
            DialogUtil.showErrorDialog(this, "Chưa đủ thông tin", "Vui lòng nhập Địa Chỉ Chính Xác của Bất Động Sản",
                    getString(R.string.ok), null)
            return
        }

        CreateSecondaryStep3Activity.start(this@CreateSecondaryStep2Activity)
        finish()
    }

    private fun isValidRequireData(): Boolean {
        if(NewSecondaryData.houseType == null || NewSecondaryData.targetHouse == null ||
                NewSecondaryData.priceUnit == null || NewSecondaryData.feeUnit == null) {
            return false
        }

        if (NewSecondaryData.name.isNullOrEmpty() || NewSecondaryData.area == null ||
                NewSecondaryData.price == null || NewSecondaryData.fee == null || NewSecondaryData.agentFee == null) {
            return false
        }
        return true
    }
}
