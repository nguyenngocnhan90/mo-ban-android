package com.moban.flow.linkmart

import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpView

interface ILinkmartView: BaseMvpView {
    fun bindingCategories(categories: List<LinkmartCategory>)
    fun bindingSpecials(linkmarts: List<Linkmart>)
    fun bindingPopular(linkmarts: List<Linkmart>)
    fun bindingNewest(linkmarts: List<Linkmart>)
    fun bindingOrders(orders: List<LinkmartOrder>)
    fun bindingCurrentLhc()
    fun bindingRewards(gifts: List<LevelGift>)
    fun purchaseFailed(error: String? = null)
    fun markUseCompleted(gifts: List<LevelGift>, gift: LevelGift)
    fun purchaseCompleted()
}
