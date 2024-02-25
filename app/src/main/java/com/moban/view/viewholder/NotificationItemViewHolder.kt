package com.moban.view.viewholder

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.notification.Notification
import com.moban.util.Font
import kotlinx.android.synthetic.main.item_notification.view.*

/**
 * Created by LenVo on 5/11/18.
 */
class NotificationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        private val UNREAD_COLOR = "#F5A623"
        private val READ_COLOR = "#4F9C3A"
    }

    fun bind(notification: Notification) {
        val context = itemView.context

        val fontRegular = Font.regularTypeface(context)
        arrayOf(itemView.item_notification_title,
                itemView.item_notification_body)
                .forEach {
                    it.typeface = fontRegular
                }

        itemView.item_notification_title.text = notification.title
        itemView.item_notification_body.text = notification.body

        val colorNotification = notification.getNotificationStatus()
        val drawable = context.getDrawable(R.drawable.background_round_gray)
        drawable?.setColorFilter(Color.parseColor(colorNotification.backgroundColor()), PorterDuff.Mode.MULTIPLY)

        itemView.notification_view_status.background = drawable
    }
}
