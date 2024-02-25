package com.moban.flow.projects.searchprojectadvance

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.moban.R
import com.moban.adapter.address.CityAdapter
import com.moban.adapter.address.DistrictAdapter
import com.moban.adapter.project.HostAdapter
import com.moban.enum.GACategory
import com.moban.enum.SearchForType
import com.moban.flow.projects.searchprojectresult.SearchProjectResultActivity
import com.moban.handler.CityItemHandler
import com.moban.handler.DistrictItemHandler
import com.moban.handler.HostItemHandler
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.model.param.SearchAdvanceParams
import com.moban.mvp.BaseMvpActivity
import com.moban.util.Device
import com.moban.util.DialogUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_search_project_advance.*
import kotlinx.android.synthetic.main.dialog_address.view.*
import kotlinx.android.synthetic.main.dialog_list.view.*
import kotlinx.android.synthetic.main.nav.view.*

class SearchProjectAdvanceActivity : BaseMvpActivity<ISearchProjectAdvanceView, ISearchProjectAdvancePresenter>(), ISearchProjectAdvanceView {
    override var presenter: ISearchProjectAdvancePresenter = SearchProjectAdvancePresenterIml()
    private val DEFAULT_MIN_PRICE = 0F
    private val DEFAULT_MAX_PRICE = 20F
    private val SEEKBAR_PRICE_UNIT = 500000000L
    private val DEFAULT_MIN_FEE = 1F
    private val DEFAULT_MAX_FEE = 10F

    private val types: MutableList<Int> = ArrayList()
    private var citySelected: City? = null
    private var districtSelected: District? = null
    private var hostSelected: String? = null
    private var minPrice: Int = DEFAULT_MIN_PRICE.toInt()
    private var maxPrice: Int = DEFAULT_MAX_PRICE.toInt()
    private var minFee: Int = DEFAULT_MIN_FEE.toInt()
    private var maxFee: Int = DEFAULT_MAX_FEE.toInt()

    private val hosts: MutableList<String> = ArrayList()
    private val cities: MutableList<City> = ArrayList()
    private val mapDictrict: HashMap<Int, List<District>> = HashMap()

    private val cityAdapter: CityAdapter = CityAdapter()
    private val districtAdapter: DistrictAdapter = DistrictAdapter()
    private val hostAdapter: HostAdapter = HostAdapter()
    private var searchForType = SearchForType.NONE

    companion object {
        private const val BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE"
        fun start(context: Context, searchForType: SearchForType = SearchForType.NONE) {
            val intent = Intent(context, SearchProjectAdvanceActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY_SEARCH_TYPE, searchForType.value)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search_project_advance
    }

    override fun initialize(savedInstanceState: Bundle?) {
        search_advance_nav.nav_tvTitle.text = getString(R.string.search_project_advance)
        search_advance_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_SEARCH_TYPE)) {
            searchForType = SearchForType.from(bundle?.getInt(BUNDLE_KEY_SEARCH_TYPE) ?: 0)
        }

        presenter.loadHosts()
        presenter.loadCities()

        initSeekBar()

        initScrollProjectType()

        initDialogItem()

        search_advance_btn_search.setOnClickListener {
            val param = generateSearchAdvanceParams()
            SearchProjectResultActivity.startAdvance(this@SearchProjectAdvanceActivity, param, searchForType)
        }

        search_advance_btn_clear.setOnClickListener {
            clearSearch()
        }

        setGAScreenName("Search Project Advance", GACategory.SEARCH)
    }

    private fun clearSearch() {
        citySelected = null
        districtSelected = null
        hostSelected = null
        minPrice = DEFAULT_MIN_PRICE.toInt()
        maxPrice = DEFAULT_MAX_PRICE.toInt()
        minFee = DEFAULT_MIN_FEE.toInt()
        maxFee = DEFAULT_MAX_FEE.toInt()

        val textColor = Util.getColor(this, R.color.color_black_30)
        search_advance_tv_host.setTextColor(textColor)
        search_advance_tv_host.text = getString(R.string.select)

        search_advance_tv_city.setTextColor(textColor)
        search_advance_tv_city.text = getString(R.string.select)

        search_advance_tv_district.setTextColor(textColor)
        search_advance_tv_district.text = getString(R.string.select)

        val projectBtnArr = arrayOf(search_advance_img_villas,
                search_advance_img_house, search_advance_img_apartment, search_advance_img_dat_nen,
                search_advance_img_shop_house, search_advance_img_condotel, search_advance_img_office_tel)

        val projectCheckImgArr = arrayOf(search_advance_img_check_villas,
                search_advance_img_check_house, search_advance_img_check_apartment, search_advance_img_check_dat_nen,
                search_advance_img_check_shop_house, search_advance_img_check_condotel, search_advance_img_check_office_tel)
        selectAllProjectType(projectBtnArr, projectCheckImgArr)

        initDataSeekBar()
    }

    private fun selectAllProjectType(projectBtnArr: Array<ImageView>, projectCheckImgArr: Array<ImageView>) {
        types.clear()
        projectBtnArr.forEachIndexed { index, imageView ->
            updateImageViewProjectType(imageView, projectCheckImgArr[index], false)
        }
        search_advance_btn_all.background = getDrawable(R.drawable.background_circle_dark)
        search_advance_btn_all.setTextColor(Util.getColor(this, R.color.white))
    }

    private fun generateSearchAdvanceParams(): SearchAdvanceParams {
        val searchAdvanceParams = SearchAdvanceParams()
        searchAdvanceParams.types = types
        hostSelected?.let {
            searchAdvanceParams.host = it
        }

        citySelected?.let {
            searchAdvanceParams.cityId = it.id
            districtSelected?.let {
                searchAdvanceParams.districtId = it.id
            }
        }
        searchAdvanceParams.minPrice = minPrice*SEEKBAR_PRICE_UNIT
        searchAdvanceParams.maxPrice = maxPrice*SEEKBAR_PRICE_UNIT
        searchAdvanceParams.minRate = minFee
        searchAdvanceParams.maxRate = maxFee

        return searchAdvanceParams
    }

    private fun initDialogItem() {
        search_advance_tv_city.setOnClickListener {
            val alertDialog = AlertDialog.Builder(getContext()).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.dialog_address, null) as View
            convertView.dialog_address_tv_title.text = getContext().getText(R.string.select_city)

            val linearLayoutManager = LinearLayoutManager(getContext())
            convertView.dialog_address_recycleView_city.layoutManager = linearLayoutManager
            convertView.dialog_address_recycleView_city.adapter = cityAdapter

            val districtLayoutManager = LinearLayoutManager(getContext())
            convertView.dialog_address_recycleView_district.layoutManager = districtLayoutManager
            convertView.dialog_address_recycleView_district.adapter = districtAdapter

            cityAdapter.listener = object : CityItemHandler {
                override fun onClicked(city: City) {
                    citySelected = city
                    if (!mapDictrict.containsKey(city.id)) {
                        presenter.loadDistrict(city.id)
                    } else {
                        bindingDictrictAdapter(city.id, mapDictrict[city.id]!!)
                    }

                    convertView.dialog_address_tv_city.text = city.city_Name
                    convertView.dialog_address_btn_back.visibility = View.VISIBLE
                    convertView.dialog_address_btn_close.visibility = View.GONE
                    convertView.dialog_address_tv_title.text = getContext().getString(R.string.select_district)
                    convertView.dialog_address_recycleView_city.visibility = View.GONE
                    convertView.dialog_address_district.visibility = View.VISIBLE
                }
            }

            districtAdapter.listener = object : DistrictItemHandler {
                override fun onClicked(district: District) {
                    districtSelected = district

                    search_advance_tv_city.text = citySelected!!.city_Name
                    val districtName = if (districtSelected == null) getString(R.string.all_district) else
                        districtSelected!!.district_Name
                    search_advance_tv_district.text = districtName
                    val colorDark = Util.getColor(this@SearchProjectAdvanceActivity, R.color.color_black)
                    search_advance_tv_city.setTextColor(colorDark)
                    search_advance_tv_district.setTextColor(colorDark)
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

            convertView.dialog_address_item_all_district.setOnClickListener {
                districtSelected = null

                search_advance_tv_city.text = citySelected!!.city_Name
                val districtName = if (districtSelected == null) getString(R.string.all_district) else
                    districtSelected!!.district_Name
                search_advance_tv_district.text = districtName
                val colorDark = Util.getColor(this@SearchProjectAdvanceActivity, R.color.color_black)
                search_advance_tv_city.setTextColor(colorDark)
                search_advance_tv_district.setTextColor(colorDark)
                alertDialog.dismiss()
            }

            alertDialog.setView(convertView)
            alertDialog.show()
        }

        search_advance_tv_host.setOnClickListener {
            val alertDialog = AlertDialog.Builder(getContext()).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.dialog_list, null) as View
            convertView.dialog_list_title.text = getContext().getText(R.string.select_host)

            val linearLayoutManager = LinearLayoutManager(getContext())
            convertView.dialog_list_recycle.layoutManager = linearLayoutManager
            convertView.dialog_list_recycle.adapter = hostAdapter

            hostAdapter.listener = object : HostItemHandler {
                override fun onClicked(host: String) {
                    hostSelected = host
                    val colorDark = Util.getColor(this@SearchProjectAdvanceActivity, R.color.color_black)
                    search_advance_tv_host.text = hostSelected
                    search_advance_tv_host.setTextColor(colorDark)
                    alertDialog.dismiss()
                }
            }
            convertView.dialog_list_close.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.setView(convertView)
            alertDialog.show()
        }

        search_advance_tv_district.setOnClickListener {
            if (citySelected == null) {
                DialogUtil.showErrorDialog(this, getString(R.string.error_not_select_city_title),
                        getString(R.string.error_not_select_city_message), getString(R.string.ok), null)
            } else {
                val alertDialog = AlertDialog.Builder(getContext()).create()
                val inflater = layoutInflater
                val convertView = inflater.inflate(R.layout.dialog_address, null) as View
                convertView.dialog_address_tv_title.text = getContext().getText(R.string.select_district)
                val districtLayoutManager = LinearLayoutManager(getContext())
                convertView.dialog_address_recycleView_district.layoutManager = districtLayoutManager
                convertView.dialog_address_recycleView_district.adapter = districtAdapter

                convertView.dialog_address_tv_city.text = citySelected!!.city_Name
                convertView.dialog_address_tv_title.text = getContext().getString(R.string.select_district)
                convertView.dialog_address_recycleView_city.visibility = View.GONE
                convertView.dialog_address_district.visibility = View.VISIBLE
                districtAdapter.listener = object : DistrictItemHandler {
                    override fun onClicked(district: District) {
                        districtSelected = district
                        val districtName = if (districtSelected == null) getString(R.string.all_district) else
                            districtSelected!!.district_Name
                        search_advance_tv_district.text = districtName
                        val colorDark = Util.getColor(this@SearchProjectAdvanceActivity, R.color.color_black)
                        search_advance_tv_district.setTextColor(colorDark)
                        alertDialog.dismiss()
                    }
                }
                convertView.dialog_address_btn_close.setOnClickListener {
                    alertDialog.dismiss()
                }

                convertView.dialog_address_item_all_district.setOnClickListener {
                    districtSelected = null
                    val districtName = if (districtSelected == null) getString(R.string.all_district) else
                        districtSelected!!.district_Name
                    search_advance_tv_district.text = districtName
                    val colorDark = Util.getColor(this@SearchProjectAdvanceActivity, R.color.color_black)
                    search_advance_tv_district.setTextColor(colorDark)
                    alertDialog.dismiss()
                }
                alertDialog.setView(convertView)
                alertDialog.show()
            }
        }
    }

    private fun initScrollProjectType() {
        val widthItem = 80
        val widthByDevice = Device.getScreenWidth() / 4

        val projectBoxArr = arrayOf(search_advance_box_all, search_advance_box_villas,
                search_advance_box_house, search_advance_box_apartment, search_advance_box_dat_nen,
                search_advance_box_shop_house, search_advance_box_condotel, search_advance_box_office_tel)
        projectBoxArr.forEach { relativeLayout ->
            val layoutParams = relativeLayout.layoutParams
            layoutParams.width = maxOf(widthItem, widthByDevice)
            relativeLayout.layoutParams = layoutParams
        }

        val projectBtnArr = arrayOf(search_advance_img_villas,
                search_advance_img_house, search_advance_img_apartment, search_advance_img_dat_nen,
                search_advance_img_shop_house, search_advance_img_condotel, search_advance_img_office_tel)

        val projectCheckImgArr = arrayOf(search_advance_img_check_villas,
                search_advance_img_check_house, search_advance_img_check_apartment, search_advance_img_check_dat_nen,
                search_advance_img_check_shop_house, search_advance_img_check_condotel, search_advance_img_check_office_tel)

        projectBtnArr.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val key = index + 1
                val selected = !types.contains(key)
                search_advance_btn_all.background = getDrawable(R.drawable.background_circle_border)
                search_advance_btn_all.setTextColor(Util.getColor(this, R.color.color_black))

                updateImageViewProjectType(imageView, projectCheckImgArr[index], selected)
                if (selected) {
                    types.add(key)
                } else {
                    types.remove(key)
                }
            }
        }

        search_advance_btn_all.setOnClickListener {
            selectAllProjectType(projectBtnArr, projectCheckImgArr)
        }

    }

    private fun updateImageViewProjectType(imageView: ImageView, imageViewCheck: ImageView, selected: Boolean) {
        val backgroundDrawable = if (selected) R.drawable.background_circle_dark else
            R.drawable.background_circle_border
        imageView.background = getDrawable(backgroundDrawable)

        val tintColor = if (selected) R.color.white else R.color.color_black
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(Util.getColor(this, tintColor)))

        imageViewCheck.visibility = if (selected) View.VISIBLE else View.GONE
        imageView.invalidate()
    }

    private fun initSeekBar() {
        initDataSeekBar()

        search_advancer_seekbar_price.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                minPrice = leftValue.toInt()
                maxPrice = rightValue.toInt()
                setPriceRange(leftValue.toInt(), rightValue.toInt())
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }
        })


        search_advancer_seekbar_fee.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                minFee = leftValue.toInt()
                maxFee = rightValue.toInt()
                setFeeRange(leftValue.toInt(), rightValue.toInt())
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }
        })
    }

    private fun initDataSeekBar() {
        search_advancer_seekbar_price.setIndicatorTextDecimalFormat("0")
        search_advancer_seekbar_price.setValue(DEFAULT_MIN_PRICE, DEFAULT_MAX_PRICE)
        setPriceRange(DEFAULT_MIN_PRICE.toInt(), DEFAULT_MAX_PRICE.toInt())
        search_advancer_seekbar_price.invalidate()

        search_advancer_seekbar_fee.setIndicatorTextDecimalFormat("0")
        search_advancer_seekbar_fee.setValue(DEFAULT_MIN_FEE, DEFAULT_MAX_FEE)
        setFeeRange(DEFAULT_MIN_FEE.toInt(), DEFAULT_MAX_FEE.toInt())
        search_advancer_seekbar_fee.invalidate()
    }

    private fun setPriceRange(leftValue: Int, rightValue: Int) {
        val price = getString(R.string.from_to).
                replace("\$F", getPriceFromSeekbar(leftValue)).
                replace("\$T", getPriceFromSeekbar(rightValue))

        search_advance_tv_price_range.text = price
    }

    private fun setFeeRange(leftValue: Int, rightValue: Int) {
        val fee = getString(R.string.from_to).
                replace("\$F", leftValue.toString()).
                replace("\$T", rightValue.toString()) + "%"
        search_advance_tv_fee_range.text = fee
    }

    private fun getPriceFromSeekbar(value: Int): String {
        if (value == 0) {
            return "0"
        }

        val price = value * 500
        if (price < 1000) {
            return price.toString() + " " + getString(R.string.million)
        }

        val priceBillion = price / 1000F
        return ("%.1f".format(priceBillion) + " " + getString(R.string.billion)).replace(".0", "")
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
        citySelected?.let {
            if (id == it.id) {
                districtAdapter.setDistrictsList(districts)
            }
        }
    }

    override fun bindingHosts(hostsList: List<String>) {
        hosts.clear()
        hosts.addAll(hostsList.filter { !it.isNullOrEmpty() })
        hostAdapter.setHostList(hosts)
    }
}
