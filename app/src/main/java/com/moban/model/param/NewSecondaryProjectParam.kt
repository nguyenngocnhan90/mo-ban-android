package com.moban.model.param

import com.google.gson.annotations.SerializedName

class NewSecondaryProjectParam {
    @SerializedName("house_name")
    var house_name: String? = null

    @SerializedName("house_image")
    var house_image: String? = null

    @SerializedName("house_type_id")
    var house_type_id: Int? = null

    @SerializedName("house_description")
    var house_description: String? = null

    @SerializedName("house_target_type")
    var house_target_type: Int? = null

    @SerializedName("house_usable_area")
    var house_usable_area: Double? = null

    @SerializedName("house_acreage_build")
    var house_acreage_build: Double? = null

    @SerializedName("house_acreage")
    var house_acreage: Double? = null

    @SerializedName("house_bedroom")
    var house_bedroom: Int? = null

    @SerializedName("house_wc")
    var house_wc: Int? = null

    @SerializedName("house_rear_hatch")
    var house_rear_hatch: Double? = null

    @SerializedName("house_texture")
    var house_texture: String? = null

    @SerializedName("house_direction")
    var house_direction: Int? = null

    @SerializedName("house_deck")
    var house_deck: Double? = null

    @SerializedName("house_price")
    var house_price: Int? = null

    @SerializedName("unit_house_price")
    var unit_house_price: Int? = null

    @SerializedName("host_rate")
    var host_rate: Int? = null

    @SerializedName("unit_host_rate")
    var unit_host_rate: Int? = null

    @SerializedName("house_agent_rate")
    var house_agent_rate: Int? = null

    @SerializedName("house_address")
    var house_address: String? = null

    @SerializedName("district_id")
    var district_id: Int? = null

    @SerializedName("product_id")
    var product_id: Int? = null

    @SerializedName("house_photos")
    var house_photos: MutableList<String> = ArrayList()

    @SerializedName("host_photos")
    var host_photos: MutableList<String> = ArrayList()

    @SerializedName("attributes")
    var attributes: MutableList<Int> = ArrayList()
}
