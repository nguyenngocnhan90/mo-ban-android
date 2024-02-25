package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.project.ProjectFavorite

/**
 * Created by LenVo on 5/15/18.
 */
class ProjectFavoriteResponse : BaseModel() {
    var success: Boolean = false
    var data: ProjectFavorite? = null
}
