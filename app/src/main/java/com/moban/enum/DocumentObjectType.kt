package com.moban.enum


//  Created by H. Len Vo on 6/16/2020.
//  Copyright © 2019 H. Len Vo. All rights reserved.
//

enum class DocumentObjectType(val value: String) {
    document ("document"),
    project("project"),
    post("post");

    companion object {
        fun from(value: String): DocumentObjectType? = values().firstOrNull { it.value == value }
    }
}
