package com.moban.view.viewholder

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.helper.CustomTypefaceSpan
import com.moban.model.data.document.Document
import com.moban.model.data.project.ProjectActivity
import com.moban.util.DateUtil
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_activity.view.*


/**
 * Created by LenVo on 3/17/18.
 */
class ProjectActivityItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(activity: ProjectActivity) {
        val context = itemView.context!!
        val str = SpannableStringBuilder(activity.user_Profile_Name + " " + activity.activity_Description)
        val fontBold = Typeface.createFromAsset(context.assets,
                context.resources.getString(R.string.font_bold))
        str.setSpan(CustomTypefaceSpan("", fontBold), 0, activity.user_Profile_Name.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        itemView.item_activity_title.text = str
        LHPicasso.loadImage(activity.user_Profile_Avatar, itemView.item_activity_img)
        itemView.item_activity_tv_date.text = DateUtil.dateStringFromSeconds(activity.created_Date.toLong(), Document.DF_SIMPLE_STRING)
    }
}
