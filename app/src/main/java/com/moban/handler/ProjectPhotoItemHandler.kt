package com.moban.handler

import com.moban.model.data.project.Project

interface ProjectPhotoItemHandler {
    fun onClicked(photo: Project, pos: Int)
}
