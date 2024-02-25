package com.moban.flow.secondary.create.step4

import com.moban.model.data.BitmapUpload
import com.moban.model.param.NewSecondaryProjectParam
import com.moban.mvp.BaseMvpPresenter

interface ICreateSecondaryStep4Presenter: BaseMvpPresenter<ICreateSecondaryStep4View> {
    fun uploadImages(images: ArrayList<BitmapUpload>)
    fun removeImage(image: BitmapUpload)
    fun submit(param: NewSecondaryProjectParam)
}
