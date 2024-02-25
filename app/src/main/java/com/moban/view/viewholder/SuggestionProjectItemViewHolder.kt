package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.project.Project
import com.moban.util.Font
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_suggestion_project.view.*

/**
 * Created by LenVo on 5/6/18.
 */
class SuggestionProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(project: Project) {
        val context = itemView.context

        val fontRegular = Font.regularTypeface(context)
        val fontMedium = Font.mediumTypeface(context)

        arrayOf(itemView.item_suggestion_tv_project_name)
                .forEach {
                    it.typeface = fontMedium
                }
        itemView.item_suggestion_tv_project_name.text = project.product_Name

        LHPicasso.loadImage(project.product_Image, itemView.item_suggestion_img_media)
    }

}
