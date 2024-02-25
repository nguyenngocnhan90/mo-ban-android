package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.project.Apartment

/**
 * Created by LenVo on 3/7/18.
 */
class ListApartmentResponse : BaseModel() {
    var success: Boolean = false
    var data: List<Apartment> = ArrayList()
}
