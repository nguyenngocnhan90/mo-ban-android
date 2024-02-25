package com.moban.model.data.deal

import com.google.gson.annotations.SerializedName
import com.moban.enum.DealStatus
import com.moban.enum.ApproveStatus
import com.moban.enum.DocumentType
import com.moban.model.BaseModel
import com.moban.model.data.document.Document
import com.moban.model.data.user.InterestGroup
import com.moban.util.DateUtil
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by LenVo on 3/30/18.
 */
class Deal : BaseModel() {
    var id: Int = 0

    @SerializedName("deal_Date")
    var deal_Date: Double = 0.0

    @SerializedName("customer_Name_Exact")
    var customer_Name_Exact: String = ""

    @SerializedName("flat_ID")
    var flat_ID: String = ""

    @SerializedName("flat_Price")
    var flat_Price: Long = 0

    @SerializedName("booking_Price")
    var booking_Price: Long = 0

    @SerializedName("product_Id")
    var product_Id: Int = 0

    @SerializedName("product_Name")
    var product_Name: String = ""

    @SerializedName("product_Image")
    var product_Image: String = ""

    @SerializedName("user_Id")
    var user_Id: Int = 0

    @SerializedName("deal_Agent_Revenue")
    var deal_Agent_Revenue: Long = 0

    @SerializedName("deal_Agent_Commission")
    var deal_Agent_Commission: Long = 0

    @SerializedName("deal_Leader_Commission")
    var deal_Leader_Commission: Long = 0

    @SerializedName("agent_Name")
    var agent_Name: String = ""

    @SerializedName("teamLeader_Name")
    var teamLeader_Name: String = ""

    @SerializedName("admin_Name")
    var admin_Name: String = ""

    @SerializedName("manager_Name")
    var manager_Name: String = ""

    @SerializedName("deal_Status")
    var deal_Status: Int = 1

    @SerializedName("deal_Histories")
    var deal_Histories: List<DealHistory> = ArrayList()

    @SerializedName("full_Deal_Histories")
    var full_Deal_Histories: List<DealHistory> = ArrayList()

    @SerializedName("customer_Phone")
    var customer_Phone: String = ""

    @SerializedName("customer_Payment_Method")
    var customer_Payment_Method: String = ""

    @SerializedName("customer_Interested")
    var customer_Interested: String = ""

    @SerializedName("deal_Promotion")
    var deal_Promotion: Promotion? = null

    @SerializedName("customer_Birthday")
    var customer_Birthday: String = ""

    @SerializedName("customer_CMND_Place")
    var customer_CMND_Place: String = ""

    @SerializedName("customer_Address")
    var customer_Address: String = ""

    @SerializedName("customer_Address_City")
    var customer_Address_City: String = ""

    @SerializedName("customer_Address_District")
    var customer_Address_District: String = ""

    @SerializedName("customer_CMND_Date")
    var customer_CMND_Date: String = ""

    @SerializedName("customer_CMND")
    var customer_CMND: String = ""

    @SerializedName("customer_Email")
    var customer_Email: String = ""

    @SerializedName("customer_Address_Permanent")
    var customer_Address_Permanent: String = ""

    @SerializedName("customer_Address_Permanent_City")
    var customer_Address_Permanent_City: String = ""

    @SerializedName("customer_Address_Permanent_District")
    var customer_Address_Permanent_District: String = ""

    @SerializedName("approve_Status")
    var approve_Status: Int = 0

    @SerializedName("approve_Status_Date")
    var approve_Status_Date: Double = 0.0

    @SerializedName("booking_Index")
    var booking_Index: Int = 0

    @SerializedName("documents")
    var documents: List<Document> = ArrayList()

    @SerializedName("deal_Interests")
    var deal_Interests: List<InterestGroup> = ArrayList()

    @SerializedName("deal_Comment")
    var deal_Comment: String = ""

    @SerializedName("has_Review")
    var has_Review: Boolean = false

    @SerializedName("can_Review")
    var can_Review: Boolean = false

    @SerializedName("can_Review_QLDT")
    var can_Review_QLDT: Boolean = false

    @SerializedName("admin_Avatar")
    var admin_Avatar: String = ""

    @SerializedName("admin_Phone")
    var admin_Phone: String = ""

    @SerializedName("teamLeader_Avatar")
    var teamLeader_Avatar: String = ""

    @SerializedName("deal_Price_String")
    var deal_Price_String: String = ""

    @SerializedName("deal_Date_String")
    var deal_Date_String: String = ""

    @SerializedName("deal_Status_String")
    var deal_Status_String: String = ""

    @SerializedName("deal_Status_Color")
    var deal_Status_Color: String = ""

    fun getDealDate(): String {
        return DateUtil.dateStringFromSeconds(deal_Date.toLong(), "dd/MM/yyyy")
    }

    fun getFullPrice(): String {
        return NumberFormat.getNumberInstance(Locale.US).format(booking_Price)
    }

    fun getDealStatus(): DealStatus? {
        return DealStatus.fromNullable(deal_Status)
    }

    fun getApproveStatus(): ApproveStatus {
        return ApproveStatus.from(approve_Status) ?: ApproveStatus.Waiting
    }

    fun isRejected(): Boolean {
        return getDealStatus() == DealStatus.CANCELED || getApproveStatus() == ApproveStatus.Denied
    }

    fun canEdit(): Boolean {
        return approve_Status == ApproveStatus.Waiting.value
                || approve_Status == ApproveStatus.WatingForFixing.value
    }

    fun canCancel(): Boolean {
        return approve_Status != ApproveStatus.Approved.value
                && deal_Status == DealStatus.BOOKED.value
    }

    fun canRefund(): Boolean {
        return approve_Status == ApproveStatus.Approved.value
                && deal_Status == DealStatus.BOOKED.value
    }

    fun canDeposit(): Boolean {
        return approve_Status == ApproveStatus.Approved.value
                && deal_Status == DealStatus.BOOKED.value
    }

    fun canMakeContract(): Boolean {
        return approve_Status == ApproveStatus.Approved.value
                && deal_Status == DealStatus.DEPOSITED.value
    }

    fun hasComment(): Boolean {
        return !deal_Comment.isNullOrEmpty()
    }

    fun isWaitingForFixing(): Boolean {
        return approve_Status == ApproveStatus.WatingForFixing.value
    }

    private fun endFixingDate(): Double {
        return approve_Status_Date + (15 * 60).toDouble()
    }

    fun isInFixingTime(): Boolean {
        val now = DateUtil.currentTimeSeconds()
        return isWaitingForFixing()
                && now >= approve_Status_Date && now < endFixingDate()
    }

    fun remainingFixingSecond(): Double {
        return endFixingDate() - DateUtil.currentTimeSeconds()
    }

    fun remainingFixingTimeString(): String {
        val remaining = remainingFixingSecond().toInt()
        val min = remaining / 60
        val sec = remaining % 60

        val minString = if(min < 10) "0" else "" + "$min"
        val secString = if(sec < 10) "0" else "" + "$sec"
        return "$minString:$secString"
    }

    fun homeAddress(): String {
        if (customer_Address_Permanent.isNullOrEmpty() && customer_Address_Permanent_District.isNullOrEmpty() && customer_Address_Permanent_City.isNullOrEmpty()) {
            return ""
        }
        return customer_Address_Permanent + "\n" + customer_Address_Permanent_District + ", " + customer_Address_Permanent_City
    }

    fun address(): String {
        if (customer_Address.isNullOrEmpty() && customer_Address_District.isNullOrEmpty() && customer_Address_City.isNullOrEmpty()) {
            return ""
        }
        return customer_Address + "\n" + customer_Address_District + ", " + customer_Address_City
    }

    fun finishedHistories(): List<DealHistory> {
        return full_Deal_Histories.filter {
            it.is_Finished
        }
    }

    fun bank_Invoice_Documents(): List<Document> {
        return documents.filter { it.document_Type_Id == DocumentType.INVOICE.value }
                .filter { it.link.isNotEmpty() }
    }

    fun cash_Invoice_Documents(): List<Document> {
        return documents.filter { it.document_Type_Id == DocumentType.cashInvoice.value }
                .filter { it.link.isNotEmpty() }
    }

    fun deposit_Documents(): List<Document> {
        return documents.filter { it.document_Type_Id == DocumentType.phieuCoc.value }
                .filter { it.link.isNotEmpty() }
    }

    fun contract_Documents(): List<Document> {
        return documents.filter { it.document_Type_Id == DocumentType.hopDong_MuaBan.value }
                .filter { it.link.isNotEmpty() }
    }

    fun cmnd_Documents(): List<Document> {
        return documents.filter {
            it.document_Type_Id == DocumentType.CMND.value
                    || it.document_Type_Id == DocumentType.CMND_BACK.value
        }.filter { it.link.isNotEmpty() }
    }

    fun hoKhau_Documents(): List<Document> {
        return documents.filter {
            it.document_Type_Id == DocumentType.hoKhau_TrangBia.value
                    || it.document_Type_Id == DocumentType.hoKhau_TrangChuHo.value
                    || it.document_Type_Id == DocumentType.hoKhau_TrangKhach.value
        }.filter { it.link.isNotEmpty() }
    }
}
