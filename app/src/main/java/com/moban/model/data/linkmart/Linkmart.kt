package com.moban.model.data.linkmart

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Linkmart: BaseModel() {
    var id: Int = 0
    var name: String = ""

    var description: String = ""

    @SerializedName("short_description")
    var shortDescription: String = ""

    @SerializedName("storeId")
    var storeId: String = ""

    @SerializedName("minOrderQty")
    var minOrderQty: Int = 0

    @SerializedName("rewardAmount")
    var rewardAmount: Int = 0

    var price: String = ""

    @SerializedName("regular_price")
    var regularPrice: String = ""

    @SerializedName("sale_price")
    var salePrice: String? = null

    var quantity: Int = 0
    var weight: String = ""
    var length: String = ""
    var height: String = ""
    var width: String = ""

    var categories: List<LinkmartCategory>? = null

    var brands: List<LinkmartBrand>? = null

    @SerializedName("images")
    var images: List<LinkmartImage>? = null

    fun officalPrice(): Int {
        var string = regularPrice
        if (!salePrice.isNullOrBlank()) {
            string = salePrice!!
        }
        return string.toInt()
    }
}
