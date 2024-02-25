package com.moban.handler

import com.moban.model.data.user.Interest

/**
 * Created by LenVo on 3/30/18.
 */
interface InterestItemHandler {
    fun onSelected(interest: Interest)
    fun onUnSelected(interest: Interest)
}
