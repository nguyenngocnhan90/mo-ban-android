package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.document.Document
import com.moban.model.data.statistic.BlockStatistic

/**
 * Created by LenVo on 3/5/18.
 */
class Project: BaseModel() {
    var projectId: Int = 0

    var id: Int = 0

    @SerializedName("product_Image")
    var product_Image: String = ""

    @SerializedName("product_Name")
    var product_Name: String = ""

    @SerializedName("product_Price")
    var product_Price: Int = 0

    @SerializedName("product_Agent_Rate")
    var product_Agent_Rate: Double = 0.0

    @SerializedName("product_Agent_Rate_Text")
    var product_Agent_Rate_Text: String = ""

    var district: String = ""
    var city: String = ""

    @SerializedName("product_Documents")
    var product_Documents: String? = null

    @SerializedName("product_Website")
    var product_Website: String = ""

    @SerializedName("isFavorite")
    var isFavorite: Boolean = false

    @SerializedName("user_Name")
    var user_Name: String = ""

    @SerializedName("user_Phone")
    var user_Phone: String = ""

    @SerializedName("user_Avatar")
    var user_Avatar: String = ""

    var documents: List<Document> = ArrayList()

    var blocks: List<Block> = ArrayList()

    @SerializedName("product_Description")
    var product_Description: String? = null

    @SerializedName("product_Host_Name")
    var product_Host_Name: String ? = null

    @SerializedName("product_BanGiao")
    var product_BanGiao: Double = 0.0

    @SerializedName("product_MoBan")
    var product_MoBan: Double = 0.0

    @SerializedName("product_Type")
    var product_Type: String = ""

    @SerializedName("product_Status")
    var product_Status: String = ""

    @SerializedName("product_Status_Id")
    var product_Status_Id: Int = 0

    @SerializedName("product_Icon")
    var product_Icon: String? = null

    @SerializedName("product_Bank")
    var product_Bank: String = ""

    @SerializedName("product_Address")
    var product_Address: String = ""

    @SerializedName("product_Location")
    var product_Location: String = ""

    var statistics: BlockStatistic = BlockStatistic()

    @SerializedName("with_Dat_Cho")
    var with_Dat_Cho: Boolean = false

    @SerializedName("with_Cart")
    var with_Cart: Boolean = false

    @SerializedName("isBookable")
    var isBookable: Boolean? = null

    @SerializedName("market_Place_Url")
    var market_Place_Url: String = ""

    @SerializedName("quyhoach_Url")
    var quyhoach_Url: String = ""

    @SerializedName("maxBookingIndex")
    var maxBookingIndex: Int = 0

    @SerializedName("is_required_deal_info")
    var is_required_deal_info: Boolean = false

    @SerializedName("deal_Deposit")
    var deal_Deposit: Int? = null

    @SerializedName("advance_Rate")
    var advance_Rate: String = ""

    @SerializedName("commission_Rate_Agent")
    var commission_Rate_Agent: String = ""

    @SerializedName("deal_Price")
    var deal_Price: String = ""

    @SerializedName("remain_Carts")
    var remain_Carts: Int = 0

    @SerializedName("service_Fee")
    var service_Fee: String = ""
    
    @SerializedName("service_Fee_Discount")
    var service_Fee_Discount: String = ""

    @SerializedName("is_Login_Required")
    var is_Login_Required: Boolean = false

    @SerializedName("rating")
    var rating: ProjectRating? = null

    @SerializedName("my_Rating")
    var my_Rating: ProjectRating? = null

    fun fullAddress(): String {
        return ( "$district, $city")
    }

    fun priceString(): String {
        val price = product_Price.toFloat() / 1000000.0
        var string = String.format("%.1f", price)
        if (string.contains(",0") || string.contains(".0")) {
            string = string.replace(",0","").replace(".0","")
        }
        return string
    }

    fun product_Host_Name_String(): String {
        return if(product_Host_Name.isNullOrEmpty()) "--" else product_Host_Name!!
    }

    fun isBookingUnavailable(): Boolean {
        return !with_Dat_Cho && !with_Cart
    }

    fun isCartAvailable(): Boolean {
        return with_Cart && blocks.isNotEmpty()
    }

    fun depositPrice(): Int {
        return deal_Deposit ?: 0
    }

    fun finalDiscountServiceFee(): String {
        if (service_Fee_Discount.isNotEmpty()) {
            return service_Fee_Discount
        }

        return service_Fee
    }

    fun finalServiceFee(): String {
        if (service_Fee_Discount == "") {
            return ""
        }

        if (service_Fee_Discount == service_Fee) {
            return ""
        }

        return service_Fee
    }
}
