package com.moban.handler

import com.moban.model.data.deal.Promotion

interface PromotionDialogHandler {
    fun onSelected(promotion: Promotion)
}