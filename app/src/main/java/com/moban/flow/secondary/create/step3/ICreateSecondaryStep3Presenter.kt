package com.moban.flow.secondary.create.step3

import com.moban.model.data.BitmapUpload
import com.moban.mvp.BaseMvpPresenter

interface ICreateSecondaryStep3Presenter: BaseMvpPresenter<ICreateSecondaryStep3View> {
    fun uploadImages(images: ArrayList<BitmapUpload>)
    fun removeImage(image: BitmapUpload)
}
