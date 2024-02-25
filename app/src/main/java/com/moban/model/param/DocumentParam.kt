package com.moban.model.param

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class DocumentParam: BaseModel() {
    @SerializedName("document_type_id")
    var document_type_id: Int = 0

    var link: String = ""
}
