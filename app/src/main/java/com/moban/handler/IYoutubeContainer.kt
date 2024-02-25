package com.moban.handler

import android.widget.FrameLayout
import com.moban.flow.youtube.YoutubeVideoFragment

interface IYoutubeContainer {
    fun sendLoggingEvent(action: String)
    fun getFlVideoContainer() : FrameLayout
    fun getYoutubeVideoFragment() : YoutubeVideoFragment
    fun getViewYoutubePos(): Int
    fun setViewYoutubePos(position: Int)
    fun handleOnVideoFullScreen(isFullScreen: Boolean)
    fun checkYouTubeViewOverlay()
    fun releaseYouTubePlayer()
}
