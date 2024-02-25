package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class UserCoin : BaseModel() {
    var id: Int = 0

    @SerializedName("total_Coin")
    var total_Coin: Int = 0

    @SerializedName("current_Coin")
    var current_Coin: Int = 0

    @SerializedName("coin_Revenue")
    var coin_Revenue: Int = 0

    @SerializedName("coin_Activity")
    var coin_Activity: Int = 0

    @SerializedName("coin_Revenue_Ranking")
    var coin_Revenue_Ranking: Int = 0

    @SerializedName("linkPoint")
    var linkPoint: Int = 0

    @SerializedName("levelPoint")
    var levelPoint: Int = 0
}
