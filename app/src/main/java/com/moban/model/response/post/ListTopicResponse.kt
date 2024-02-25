package com.moban.model.response.post

import com.moban.model.BaseModel

class ListTopicResponse: BaseModel() {
    var success: Boolean = false
    var data: ListTopics? = null
}