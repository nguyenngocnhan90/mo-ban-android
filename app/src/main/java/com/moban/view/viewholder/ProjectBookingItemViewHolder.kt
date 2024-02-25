package com.moban.view.viewholder

import android.content.Context
import android.os.CountDownTimer
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.enum.CartStatus
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Project
import com.moban.util.LHPicasso
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_booking.view.*

/**
 * Created by LenVo on 3/21/18.
 */
class ProjectBookingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private var projectBooking: ProjectBooking? = null
    private var context: Context? = null

    fun bind(projectBooking: ProjectBooking, project: Project?) {
        this.context = itemView.context
        this.projectBooking = projectBooking

        project?.product_Image?.let {
            LHPicasso.loadImage(it, itemView.item_booking_img_media)
        }

        if (projectBooking.flat_Name.isNotEmpty()) {
            val flatName = context?.getString(R.string.project_detail_apartment) + " " + projectBooking.flat_Name
            itemView.item_booking_tv_apartment_name.text = flatName
        }
        val cartStatus = CartStatus.from(projectBooking.cart_Status)

        cartStatus?.let {
            itemView.item_booking_tv_apartment_status.setTextColor(Util.getColor(context!!, cartStatus.color()))
        }
        itemView.item_booking_tv_apartment_status.text = projectBooking.cart_Status_Title


        if (!projectBooking.isOutOfTime() && (cartStatus == CartStatus.waiting || cartStatus == CartStatus.waitingAdminConfirmation)) {
            itemView.item_booking_remain_time.visibility = View.VISIBLE
            countdown(projectBooking.remainingSecond())
        }

    }

    private fun countdown(sec: Int) {
        itemView.item_booking_remain_time.visibility = View.VISIBLE

        projectBooking?.let {
            itemView.circle_count_down.max = it.timerSecond()
        }

        val countDownTimer = object : CountDownTimer((sec * 1000).toLong(), 1000) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds / 1000
                itemView.circle_count_down.progress = seconds.toInt()
                val secString = projectBooking?.remainingTimeString()
                itemView.item_booking_tv_timer.text = secString
            }

            override fun onFinish() {
                itemView.item_booking_remain_time.visibility = View.GONE
            }
        }
        countDownTimer.start()
    }

}
