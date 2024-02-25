package com.moban.model.data.booking

import com.google.gson.annotations.SerializedName
import com.moban.enum.CartStatus
import com.moban.model.BaseModel
import com.moban.model.data.project.Apartment
import com.moban.util.DateUtil
import java.util.*

/**
 * Created by LenVo on 3/19/18.
 */
class ProjectBooking : BaseModel() {
    var id: Int = 0

    @SerializedName("deal_Id")
    var deal_Id: Int = 0

    @SerializedName("due_Date")
    var due_Date: Double = 0.0

    @SerializedName("booking_Timer")
    var booking_Timer: Int = 0

    @SerializedName("product_Name")
    var product_Name: String = ""

    @SerializedName("block_Name")
    var block_Name: String = ""

    @SerializedName("flat_Name")
    var flat_Name: String = ""

    @SerializedName("flat_Info")
    var flat_Info: Apartment? = null

    @SerializedName("cart_Status")
    var cart_Status: Int = 0

    @SerializedName("cart_Status_Title")
    var cart_Status_Title: String = ""

    @SerializedName("customer_Name")
    var customer_Name: String = ""

    @SerializedName("customer_Phone")
    var customer_Phone: String = ""

    @SerializedName("customer_CMND")
    var customer_CMND: String = ""

    @SerializedName("customer_Birthday")
    var customer_Birthday: String = ""

    @SerializedName("customer_Invoice")
    var customer_Invoice: String = ""

    @SerializedName("customer_Invoice_Booking")
    var customer_Invoice_Booking: String = ""

    @SerializedName("user_Phone")
    var user_Phone: String = ""


    fun isOutOfTime(): Boolean {
        return  DateUtil.currentTimeSeconds() >= due_Date.toLong()
    }

    fun timerSecond(): Int {
        return booking_Timer * 60
    }

    fun remainingSecond(): Int {
        return (due_Date.toLong() - DateUtil.currentTimeSeconds()).toInt()
    }

    fun remainingTimeString(): String {
        val remaining = remainingSecond()

        val min = remaining.div(60)
        var minString = min.toString()
        if (min < 10) {
            minString = "0" + min.toString()
        }

        val sec = remaining.rem(60)
        var secString = sec.toString()
        if (sec < 10) {
            secString = "0" + sec.toString()
        }

        return "$minString:$secString"
    }

    fun isEditable(): Boolean {
        return cart_Status == CartStatus.waitingAdminConfirmation.value
                || cart_Status == CartStatus.waiting.value
    }
}
