package com.moban.flow.editpost

import com.moban.model.data.BitmapUpload
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by H. Len Vo on 8/25/18.
 */
interface IEditPostPresenter: BaseMvpPresenter<IEditPostView> {
    fun uploadImages(postId: Int, images: ArrayList<BitmapUpload>)
    fun removeImage(image: BitmapUpload)
    fun editPost(postId: Int, content: String, youtubeId: String, topicId: Int, images: ArrayList<BitmapUpload>)
}
