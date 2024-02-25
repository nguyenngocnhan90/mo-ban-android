package com.moban.view.custom

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.moban.util.ViewUtil
import com.yongchun.library.R
import com.yongchun.library.model.LocalMedia
import com.yongchun.library.view.ImageSelectorActivity

/**
 * Created by LenVo on 4/18/18.
 */
class CustomImageSelectorActivity : ImageSelectorActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        ViewUtil.setMarginTop(toolbar, ViewUtil.getStatusBarHeight(this))
    }

    override fun onSelectDone(medias: MutableList<LocalMedia>?) {
        super.onSelectDone(medias)
    }
}
