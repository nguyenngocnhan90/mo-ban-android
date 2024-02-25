package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.project.Project

/**
 * Created by LenVo on 3/7/18.
 */
class ProjectResponse : BaseModel() {
    var success: Boolean = false
    var data: Project? = null
}
