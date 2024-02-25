package com.moban.handler

import com.moban.model.data.user.Interest
import com.moban.model.data.user.InterestGroup

/**
 * Created by LenVo on 3/30/18.
 */
interface InterestDialogHandler {
    fun onSelected(interests: List<Interest>, selectedGroup: HashMap<String, InterestGroup>)
}
