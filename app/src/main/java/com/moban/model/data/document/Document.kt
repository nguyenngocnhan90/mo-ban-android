package com.moban.model.data.document

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.util.DateUtil
import java.util.*

/**
 * Created by LenVo on 3/5/18.
 */
class Document: BaseModel() {

    companion object {
        const val DF_SIMPLE_STRING = "dd/MM/yyyy"
    }

    var id: Int = 0

    @SerializedName("doc_Name")
    var doc_Name: String = ""

    var link: String = ""

    var image: String = ""

    @SerializedName("modified_Date")
    var modified_Date: Double = 0.0

    @SerializedName("effective_Date")
    var effective_Date: Double = 0.0

    @SerializedName("document_Type_Id")
    var document_Type_Id: Int = 0

    @SerializedName("object_Type")
    var object_Type: String = ""

    @SerializedName("object_Id")
    var object_Id: Int = 0

    @SerializedName("sold_Count")
    var sold_Count: Int = 0
    @SerializedName("stocking_Count")
    var stocking_Count: Int = 0

    @SerializedName("service_Fee")
    var service_Fee: String = ""
    @SerializedName("service_Fee_Discount")
    var service_Fee_Discount: String = ""

    fun getDocumentUpdatedDate(): String {
        return DateUtil.dateStringFromSeconds(modified_Date.toLong(), DF_SIMPLE_STRING)
    }

    fun getEffectiveDate(): String {
        return DateUtil.dateStringFromSeconds(effective_Date.toLong(), DF_SIMPLE_STRING)
    }

    fun finalDiscountServiceFee(): String {
        if (service_Fee_Discount.isNotEmpty()) {
            return service_Fee_Discount
        }

        return service_Fee
    }

    fun finalServiceFee(): String {
        if (service_Fee_Discount == "") {
            return ""
        }

        if (service_Fee_Discount == service_Fee) {
            return ""
        }

        return service_Fee
    }
}
