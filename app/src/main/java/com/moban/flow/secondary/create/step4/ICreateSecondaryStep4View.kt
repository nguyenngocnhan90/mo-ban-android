package com.moban.flow.secondary.create.step4

import com.moban.model.data.BitmapUpload
import com.moban.mvp.BaseMvpView

interface ICreateSecondaryStep4View: BaseMvpView {
    fun showUploadImageFailed(bitmapUpload: BitmapUpload)
    fun showCreateSecondarySuccess(message: String?)
    fun showCreateSecondaryFailed(message: String?)
    fun uploadComplete()
}
