package com.moban.handler

import com.moban.model.data.post.Topic

interface TopicItemHandler {
    fun onClicked(topic: Topic)
}