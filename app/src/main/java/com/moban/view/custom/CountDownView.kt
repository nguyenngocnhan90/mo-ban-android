package com.moban.view.custom

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.moban.R


/**
 * Created by H. Len Vo on 5/25/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class CountDownView: LinearLayout {
    private lateinit var hoursView: TextView
    private lateinit var minsView: TextView
    private lateinit var secsView: TextView

    var seconds: Long = 0

    interface Handler {
        fun onTimeOut()
    }
    var listener: Handler? = null
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
        View.inflate(context, R.layout.countdown, this)
        hoursView = findViewById(R.id.countdown_tv_hours)
        minsView = findViewById(R.id.countdown_tv_mins)
        secsView = findViewById(R.id.countdown_tv_secs)
    }

    fun reloadData() {
        val countDownTimer = object : CountDownTimer((seconds * 1000), 1000) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds/1000
                val hours = seconds / 3600 % 24
                val mins = seconds / 60 % 60
                val secs = seconds % 60

                val hoursString = if (hours == 0L) "00" else (if(hours < 10) "0" else "") + "$hours"
                val minString = if (mins == 0L) "00" else (if(mins < 10) "0" else "") + "$mins"
                val secsString = if (secs == 0L) "00" else (if(secs < 10) "0" else "") + "$secs"
                hoursView.text = hoursString
                minsView.text = minString
                secsView.text = secsString
            }

            override fun onFinish() {
                listener?.onTimeOut()
            }
        }
        countDownTimer.start()
    }
}
