package com.moban.flow.secondary.create.step1

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.widget.ImageViewCompat
import android.view.View
import com.moban.R
import com.moban.adapter.address.CityAdapter
import com.moban.adapter.address.DistrictAdapter
import com.moban.enum.GACategory
import com.moban.flow.secondary.create.NewSecondaryData
import com.moban.flow.secondary.create.step2.CreateSecondaryStep2Activity
import com.moban.handler.CityItemHandler
import com.moban.handler.DistrictItemHandler
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Font
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_create_secondary_step1.*
import kotlinx.android.synthetic.main.dialog_address.view.*
import kotlinx.android.synthetic.main.nav_create_secondary_project.view.*
import kotlinx.android.synthetic.main.tabbar_step_create_secondary.view.*

class CreateSecondaryStep1Activity : BaseMvpActivity<ICreateSecondaryStep1View, ICreateSecondaryStep1Presenter>(), ICreateSecondaryStep1View {
    override var presenter: ICreateSecondaryStep1Presenter = CreateSecondaryStep1PresenterIml()
    private val cities: MutableList<City> = ArrayList()
    private val mapDictrict: HashMap<Int, List<District>> = HashMap()

    private val cityAdapter: CityAdapter = CityAdapter()
    private val districtAdapter: DistrictAdapter = DistrictAdapter()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CreateSecondaryStep1Activity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_create_secondary_step1
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setupToolBar()
        presenter.loadCities()
        secondary_step1_view_select_city.setOnClickListener {
            showSelectCity()
        }

        secondary_step1_view_select_district.setOnClickListener {
            showSelectDistrict()
        }

        loadRawData()

        setGAScreenName("Create Secondary Step 1", GACategory.SECONDARY)
    }

    private fun loadRawData() {
        secondary_step1_ed_address.setText(NewSecondaryData.address)
        val colorDark = Util.getColor(this@CreateSecondaryStep1Activity, R.color.color_black)
        NewSecondaryData.city?.let {
            secondary_step1_tv_select_city.text = it.city_Name
            secondary_step1_tv_select_city.setTextColor(colorDark)
        }
        NewSecondaryData.district?.let {
            secondary_step1_tv_select_district.text = it.district_Name
            secondary_step1_tv_select_district.setTextColor(colorDark)
        }
    }

    override fun onBackPressed() {
        cancelCreateSecondary()
    }

    private fun cancelCreateSecondary() {
        var dialog: Dialog? = null
        dialog = DialogUtil.showConfirmDialog(this, true, "Hủy bỏ tạo ký gửi",
                "Lưu ý:\n Dữ liệu bạn đã nhập sẽ không được lưu lại.", getString(R.string.agree), getString(R.string.skip),
                View.OnClickListener {
                    dialog?.dismiss()
                    NewSecondaryData.reset()
                    finish()
                }, null)
    }

    private fun setupToolBar() {
        create_secondary_step1_nav.nav_img_close.visibility = View.VISIBLE
        create_secondary_step1_nav.nav_img_close.setOnClickListener {
            cancelCreateSecondary()
        }

        create_secondary_step1_nav.nav_tv_title.text = getString(R.string.address_bds_title)

        create_secondary_step1_nav.nav_continue.setOnClickListener {
            showNextStep()
        }

        tabbar_steps1.tabbar_view_step1.setBackgroundResource(R.drawable.background_circle_dark)
        ImageViewCompat.setImageTintList(tabbar_steps1.tabbar_img_step1, ColorStateList.valueOf(Util.getColor(this, R.color.white)))
    }

    private fun showSelectCity() {
        val alertDialog = AlertDialog.Builder(getContext()).create()
        val inflater = layoutInflater
        val convertView = inflater.inflate(R.layout.dialog_address, null) as View
        convertView.dialog_address_tv_title.text = getContext().getText(R.string.select_city)

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(getContext())
        convertView.dialog_address_recycleView_city.layoutManager = linearLayoutManager
        convertView.dialog_address_recycleView_city.adapter = cityAdapter

        val districtLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(getContext())
        convertView.dialog_address_recycleView_district.layoutManager = districtLayoutManager
        convertView.dialog_address_recycleView_district.adapter = districtAdapter

        cityAdapter.listener = object : CityItemHandler {
            override fun onClicked(city: City) {
                NewSecondaryData.city = city
                if (!mapDictrict.containsKey(city.id)) {
                    presenter.loadDistrict(city.id)
                } else {
                    bindingDictrictAdapter(city.id, mapDictrict[city.id]!!)
                }

                secondary_step1_tv_select_city.text = NewSecondaryData.city!!.city_Name
                val colorDark = Util.getColor(this@CreateSecondaryStep1Activity, R.color.color_black)
                secondary_step1_tv_select_city.setTextColor(colorDark)

                val boldFont = Font.boldTypeface(this@CreateSecondaryStep1Activity)
                convertView.dialog_address_tv_city.typeface = boldFont

                convertView.dialog_address_tv_city.text = city.city_Name
                convertView.dialog_address_btn_back.visibility = View.VISIBLE
                convertView.dialog_address_btn_close.visibility = View.GONE
                convertView.dialog_address_tv_title.text = getContext().getString(R.string.select_district)
                convertView.dialog_address_recycleView_city.visibility = View.GONE
                convertView.dialog_address_district.visibility = View.VISIBLE
                convertView.dialog_address_tv_all_district.visibility = View.GONE

                convertView.dialog_address_item_all_district.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }

        districtAdapter.listener = object : DistrictItemHandler {
            override fun onClicked(district: District) {
                NewSecondaryData.district = district

                secondary_step1_tv_select_city.text = NewSecondaryData.city!!.city_Name
                val districtName = if (NewSecondaryData.district == null) "" else
                    NewSecondaryData.district!!.district_Name
                secondary_step1_tv_select_district.text = districtName
                val colorDark = Util.getColor(this@CreateSecondaryStep1Activity, R.color.color_black)
                secondary_step1_tv_select_city.setTextColor(colorDark)
                secondary_step1_tv_select_district.setTextColor(colorDark)
                alertDialog.dismiss()
            }
        }

        convertView.dialog_address_btn_back.setOnClickListener {
            convertView.dialog_address_btn_back.visibility = View.GONE
            convertView.dialog_address_btn_close.visibility = View.VISIBLE
            convertView.dialog_address_tv_title.text = getContext().getString(R.string.select_city)
            convertView.dialog_address_recycleView_city.visibility = View.VISIBLE
            convertView.dialog_address_district.visibility = View.GONE
        }

        convertView.dialog_address_btn_close.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setView(convertView)
        alertDialog.show()
    }

    private fun showSelectDistrict() {
        if (NewSecondaryData.city == null) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_not_select_city_title),
                    getString(R.string.error_not_select_city_message), getString(R.string.ok), null)
        } else {
            val alertDialog = AlertDialog.Builder(getContext()).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.dialog_address, null) as View
            convertView.dialog_address_tv_title.text = getContext().getText(R.string.select_district)
            val districtLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(getContext())
            convertView.dialog_address_recycleView_district.layoutManager = districtLayoutManager
            convertView.dialog_address_recycleView_district.adapter = districtAdapter
            val boldFont = Font.boldTypeface(this)
            convertView.dialog_address_tv_city.typeface = boldFont
            convertView.dialog_address_tv_city.text = NewSecondaryData.city!!.city_Name
            convertView.dialog_address_tv_title.text = getContext().getString(R.string.select_district)
            convertView.dialog_address_recycleView_city.visibility = View.GONE
            convertView.dialog_address_tv_all_district.visibility = View.GONE
            convertView.dialog_address_district.visibility = View.VISIBLE
            districtAdapter.listener = object : DistrictItemHandler {
                override fun onClicked(district: District) {
                    NewSecondaryData.district = district
                    val districtName = if (NewSecondaryData.district == null) "" else
                        NewSecondaryData.district!!.district_Name
                    secondary_step1_tv_select_district.text = districtName
                    val colorDark = Util.getColor(this@CreateSecondaryStep1Activity, R.color.color_black)
                    secondary_step1_tv_select_district.setTextColor(colorDark)
                    alertDialog.dismiss()
                }
            }
            convertView.dialog_address_btn_close.setOnClickListener {
                alertDialog.dismiss()
            }

            convertView.dialog_address_item_all_district.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.setView(convertView)
            alertDialog.show()
        }
    }

    private fun showNextStep() {
        NewSecondaryData.address = secondary_step1_ed_address.text.toString()

        if (NewSecondaryData.district == null) {
            DialogUtil.showErrorDialog(this, "Chưa đủ thông tin", "Vui lòng chọn đầy đủ Tỉnh/Thành Phố và Quận/Huyện",
                    getString(R.string.ok), null)
            return
        }

        if (NewSecondaryData.address.isNullOrEmpty()) {
            DialogUtil.showErrorDialog(this, "Chưa đủ thông tin", "Vui lòng nhập Địa Chỉ Chính Xác của Bất Động Sản",
                    getString(R.string.ok), null)
            return
        }
        CreateSecondaryStep2Activity.start(this@CreateSecondaryStep1Activity)
        finish()
    }

    override fun bindingCities(citiesList: List<City>) {
        cities.clear()
        cities.addAll(citiesList)

        cityAdapter.setCitiesList(citiesList)
    }

    override fun bindingDistrict(id: Int, districts: List<District>) {
        mapDictrict.put(id, districts)
        bindingDictrictAdapter(id, districts)
    }

    private fun bindingDictrictAdapter(id: Int, districts: List<District>) {
        NewSecondaryData.city?.let {
            if (id == it.id) {
                districtAdapter.setDistrictsList(districts)
            }
        }
    }
}
