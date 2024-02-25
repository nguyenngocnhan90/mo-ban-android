package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.DataInfo
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by LenVo on 3/18/18.
 */
class Apartment : BaseModel() {
    var id: Int = 0

    @SerializedName("product_Name")
    var product_Name: String = ""

    @SerializedName("flat_Name")
    var flat_Name: String = ""

    @SerializedName("block_Name")
    var block_Name: String = ""

    @SerializedName("floor_Name")
    var floor_Name: String = ""

    @SerializedName("flat_Price_Without_VAT")
    var flat_Price_Without_VAT: Long = 0

    @SerializedName("flat_Price")
    var flat_Price: Long = 0

    @SerializedName("flat_Status")
    var flat_Status: Int = 0

    @SerializedName("flat_Status_Title")
    var flat_Status_Title: String = ""

    @SerializedName("flat_Area")
    var flat_Area: String = ""

    @SerializedName("isBookable")
    var isBookable: Boolean = true

    @SerializedName("note")
    var note: String = ""

    var info: List<DataInfo> = ArrayList()

    fun priceInBillion(): String {
        var price = flat_Price.toFloat() / 1000000000.0
        var string = String.format("%.1f", price)
        if (string.contains(",0") || string.contains(".0")) {
            string = string.replace(",0","").replace(".0","")
        }
        return string
    }

    fun getFullPriceString(): String {
        return NumberFormat.getNumberInstance(Locale.US).format(flat_Price)
    }

    fun getWithoutVATPriceString(): String {
        return NumberFormat.getNumberInstance(Locale.US).format(flat_Price_Without_VAT)
    }
}
