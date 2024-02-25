package com.moban.handler

import com.moban.model.data.deal.Promotion

interface PromotionItemHandler {
    fun onSelected(promotion: Promotion)
}