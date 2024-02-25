package com.moban.handler

import com.moban.model.data.user.Badge

/**
 * Created by LenVo on 3/30/18.
 */
interface BadgeItemHandler {
    fun onClicked(badge: Badge)
}
