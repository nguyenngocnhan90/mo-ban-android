package com.moban.flow.newpost

import com.moban.model.data.BitmapUpload
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/17/18.
 */
interface INewPostPresenter : BaseMvpPresenter<INewPostView> {
    fun uploadImages(images: ArrayList<BitmapUpload>)
    fun createPost(content: String, youtubeId: String, topicId: Int, images: ArrayList<BitmapUpload>, ratingId: Int, projectId: Int)
    fun removeImage(image: BitmapUpload)
}
