package com.moban.model.response.post

import com.moban.model.BaseModel
import com.moban.model.data.post.Topic

class ListTopics : BaseModel() {
    var list: List<Topic> = ArrayList()
}