package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.ProjectPhotoItemHandler
import com.moban.model.data.project.Project
import com.moban.view.viewholder.PhotoItemViewHolder

/**
 * Created by LenVo on 3/17/19.
 */
class ProjectPhotoAdapter : AbsAdapter<PhotoItemViewHolder>() {

    var listener: ProjectPhotoItemHandler? = null

    var photos: MutableList<Project> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PhotoItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photos.count()
    }

    override fun updateView(holder: PhotoItemViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)

        holder.itemView.setOnClickListener {
            listener?.onClicked(photo, position)
        }
    }

    fun setPhotosList(photos: List<Project>) {
        this.photos.clear()
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }

}
