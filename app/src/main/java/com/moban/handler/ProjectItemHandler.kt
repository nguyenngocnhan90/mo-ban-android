package com.moban.handler

import com.moban.model.data.project.Project

/**
 * Created by LenVo on 3/11/18.
 */
interface ProjectItemHandler {
    fun onClicked(project : Project)
    fun onFavorite(project : Project)
}
