package com.moban.flow.secondary.create.step3

import com.moban.model.data.BitmapUpload
import com.moban.mvp.BaseMvpView

interface ICreateSecondaryStep3View: BaseMvpView {
    fun showUploadImageFailed(bitmapUpload: BitmapUpload)
    fun uploadComplete()
}
