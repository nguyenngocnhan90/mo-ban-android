package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.enum.Rank
import com.moban.helper.LocalStorage
import com.moban.model.BaseModel
import com.moban.model.data.popup.Popup
import java.text.NumberFormat
import java.util.*

/**
 * Created by neal on 3/3/18.
 */
class User : BaseModel() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("email")
    var email: String = ""

    @SerializedName("username")
    var username: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("address")
    var address: String = ""

    @SerializedName("gender")
    var gender: String = ""

    @SerializedName("birthday")
    var birthday: String = ""

    @SerializedName("entrancedate")
    var entrancedate: Double = 0.0

    @SerializedName("branch")
    var branch: String = ""

    @SerializedName("can_Post")
    var can_Post: Boolean = false

    @SerializedName("role_Id")
    var role_Id: Int = 0

    @SerializedName("role")
    var role: String = ""

    @SerializedName("token")
    var token: String = ""

    @SerializedName("linkmart_Token")
    var linkmart_token: String = ""

    @SerializedName("linkmart_Customer_Id")
    var linkmart_customer_id: String? = null

    @SerializedName("total_Coin")
    var total_Coin: Int = 0

    @SerializedName("current_Coin")
    var current_Coin: Int = 0

    @SerializedName("current_Rank")
    var current_Rank: Int = 0

    @SerializedName("current_Rank_Name")
    var current_Rank_Name: String = ""

    @SerializedName("next_Coin")
    var next_Coin: Int = 0

    @SerializedName("favorite_products_count")
    var favorite_products_count: Int = 0

    @SerializedName("user_Coins")
    var user_Coins: UserCoin? = null

    @SerializedName("is_ForcedToChangePassword")
    var is_ForcedToChangePassword: Boolean = false

    @SerializedName("isBO")
    var isBO: Boolean = false

    @SerializedName("current_Level")
    var current_Level: Int = 0

    @SerializedName("current_Level_Name")
    var current_Level_Name: String = ""

    @SerializedName("current_Level_DT")
    var current_Level_DT: Int = 0

    @SerializedName("current_Level_DT_Name")
    var current_Level_DT_Name: String = ""

    @SerializedName("next_Level_DT")
    var next_Level_DT: Int = 0

    @SerializedName("next_Level_DT_Name")
    var next_Level_DT_Name: String = ""

    @SerializedName("next_Level_QLDT")
    var next_Level_QLDT: Int = 0

    @SerializedName("next_Level_QLDT_Name")
    var next_Level_QLDT_Name: String = ""

    @SerializedName("current_Level_QLDT")
    var current_Level_QLDT: Int = 0

    @SerializedName("current_Level_QLDT_Name")
    var current_Level_QLDT_Name: String = ""

    @SerializedName("next_Level_GD")
    var next_Level_GD: Int = 0

    @SerializedName("next_Level_GD_Name")
    var next_Level_GD_Name: String = ""

    @SerializedName("next_LevelPoint")
    var next_LevelPoint: Int = 0

    @SerializedName("next_LinkPoint")
    var next_LinkPoint: Int = 0

    @SerializedName("unreview_Deal_Count")
    var unreview_Deal_Count: Int = 0

    @SerializedName("all_Deal_Count")
    var all_Deal_Count: Int = 0

    @SerializedName("popup")
    var popup: Popup? = null

    @SerializedName("isOutsideAgent")
    var isOutsideAgent: Boolean = false

    @SerializedName("is_Phone_Verified")
    var is_Phone_Verified: Boolean = false

    @SerializedName("access_Type")
    var access_Type: String = ""

    fun getRank() : Rank {
        var rank = Rank.MEMBER
        for (item in Rank.list) {
            if (item.value == current_Rank) {
                rank = item
                break
            }
        }
        return rank
    }

    fun getMissingCoin() : Int {
        return next_Coin - current_Coin
    }

    fun getMissingRevenueCoin() : Int {
        val coin = user_Coins?.coin_Revenue_Ranking ?: 0
        return next_Coin - coin
    }

    fun isCurrentUser(): Boolean {
        val loggedInId = LocalStorage.user()?.id ?: 0
        return id == loggedInId
    }

    fun isAdmin(): Boolean {
        return role_Id == 5
    }

    fun rankColor(): String {
        if (isBO) {
            return "#207FD1"
        }
        return getRank().color()
    }

    fun validateRankName(): String {
        if (isBO) {
            return "B.O"
        }

        return current_Level_Name
    }

    fun currentCoinString(): String {
        return NumberFormat.getNumberInstance(Locale.US).format(current_Coin)
    }

    fun rankLevelNumber(isPartnerManager: Boolean): String {
        if (isBO) {
            return "B.O"
        }

        var fullLevel = current_Level_DT_Name
        if (isPartnerManager && getRank() == Rank.PARTNER_MANAGER) {
            fullLevel = current_Level_QLDT_Name
        }

        return fullLevel.split(" ").last()
    }

    fun isAnonymous(): Boolean {
        return access_Type == "anonymous"
    }

}
