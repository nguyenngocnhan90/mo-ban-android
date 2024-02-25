package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.model.data.secondary.SecondaryProject
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_secondary_project.view.*

/**
 * Created by LenVo on 3/11/19.
 */
class SecondaryProjectItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(project: SecondaryProject, position: Int) {

        itemView.item_secondary_project_tvName.text = project.product_Name
        LHPicasso.loadImage(project.product_Image, itemView.item_secondary_project_imgMedia)
    }

}
