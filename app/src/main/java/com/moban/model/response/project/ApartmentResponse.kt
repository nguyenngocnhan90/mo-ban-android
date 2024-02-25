package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.project.Apartment

/**
 * Created by LenVo on 3/19/18.
 */
class ApartmentResponse : BaseModel() {
    var success: Boolean = false
    var data: Apartment? = null
}
