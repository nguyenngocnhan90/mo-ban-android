package com.moban.util

import android.content.Context
import android.graphics.Typeface
import com.moban.R

class Font {
    companion object {

        fun regularTypeface(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets,
                    context.getString(R.string.font_regular))
        }

        fun mediumTypeface(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets,
                    context.getString(R.string.font_medium))
        }

        fun boldTypeface(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets,
                    context.getString(R.string.font_bold))
        }

        fun typefaceByFontId(context: Context, fontId: Int): Typeface {
            return Typeface.createFromAsset(context.assets,
                    context.getString(fontId))
        }
    }
}
