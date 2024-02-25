package com.moban.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.moban.R

/**
 * Created by neal on 3/12/18.
 */
class ImageViewerOverlay : RelativeLayout {

    var buttonClose: RelativeLayout? = null
    var buttonDownload: Button? = null
    var image_viewer_content: View? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.view_image_viewer_overlay, this)
        buttonClose = view.findViewById(R.id.image_viewer_overlay_btn_close)
        buttonDownload = view.findViewById(R.id.image_viewer_overlay_btn_download)
        image_viewer_content = view.rootView
    }
}
