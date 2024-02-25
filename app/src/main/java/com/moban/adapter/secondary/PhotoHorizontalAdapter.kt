package com.moban.adapter.secondary

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.PhotoHorizontalBottomMenuHandler
import com.moban.handler.PhotoHorizontalHandler
import com.moban.model.data.BitmapUpload
import com.moban.util.DialogBottomSheetUtil
import com.moban.view.viewholder.PhotoHorizontalItemViewHolder

class PhotoHorizontalAdapter : AbsAdapter<PhotoHorizontalItemViewHolder>() {
    var listener: PhotoHorizontalHandler? = null
    var mainPhoto: BitmapUpload? = null

    var photos: MutableList<BitmapUpload> = ArrayList()
    var enableMainPhoto = true
    var enableEditMenuPhoto = true
    var showAddPhoto = true

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_horizontal_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHorizontalItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PhotoHorizontalItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photos.count() + (if (showAddPhoto) 1 else 0)
    }

    override fun updateView(holder: PhotoHorizontalItemViewHolder, position: Int) {
        if (position == photos.count() && showAddPhoto) {
            holder.bindCreatePhoto()
            holder.itemView.setOnClickListener {
                listener?.onAddPhoto()
            }
            return
        }

        val photo = photos[position]

        val isMainPhoto = mainPhoto != null && mainPhoto!!.time == photo.time && enableMainPhoto
        if (isMainPhoto) {
            holder.bindMainPhoto(photo)
        } else {
            holder.bind(photo)
        }

        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            if (enableEditMenuPhoto) {
                var dialog: Dialog? = null
                dialog = DialogBottomSheetUtil.showDialogListActionPhotoSecondary(context, isMainPhoto || !enableMainPhoto,
                        object : PhotoHorizontalBottomMenuHandler {
                            override fun onDeletePhoto() {
                                dialog?.dismiss()
                                listener?.onDeletePhoto(photo)
                            }

                            override fun onSetMainPhoto() {
                                dialog?.dismiss()
                                listener?.onSetMainPhoto(photo)
                            }
                        })
            }
        }

    }

    fun setPhotosList(photos: List<BitmapUpload>) {
        this.photos.clear()
        this.photos.addAll(photos)
        if (enableMainPhoto && mainPhoto == null && photos.isNotEmpty()) {
            mainPhoto = photos[0]
        }
        notifyDataSetChanged()
    }

    fun addPhotosList(photos: List<BitmapUpload>) {
        this.photos.addAll(photos)
        if (enableMainPhoto && mainPhoto == null && !this.photos.isNotEmpty()) {
            mainPhoto = this.photos[0]
        }
        notifyDataSetChanged()
    }

    fun updateMainPhoto(photo: BitmapUpload) {
        mainPhoto = photo
        notifyDataSetChanged()
    }

    fun updatePhoto(photo: BitmapUpload, forceRefresh: Boolean = false) {
        for (item in this.photos) {
            if (item.time == photo.time) {
                item.uploaded = photo.uploaded
                item.bitmap = photo.bitmap
                item.url = photo.url
                break
            }
        }
        if (forceRefresh) {
            notifyDataSetChanged()
        }
    }

    fun removePhoto(photo: BitmapUpload) {
        for (item in this.photos) {
            if (item.time == photo.time) {
                this.photos.remove(item)
                break
            }
        }
        //Check delete main photo
        if (enableMainPhoto && mainPhoto != null && mainPhoto!!.time == photo.time && this.photos.isNotEmpty()) {
            mainPhoto = this.photos[0]
        }
        notifyDataSetChanged()
    }

    fun checkUploadCompleted(): Boolean {
        for (item in this.photos) {
            if (!item.isUploaded()) {
                return false
            }
        }
        return true
    }
}
