package com.moban.handler

import com.moban.model.data.statistic.Statistic

interface ProjectTypeItemHandler {
    fun onSelected(type: Statistic)
}
