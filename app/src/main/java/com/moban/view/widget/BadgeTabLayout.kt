package com.moban.view.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.Nullable
import com.google.android.material.tabs.TabLayout
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.moban.R


/**
 * https://gist.github.com/eneim/55df01e092520a6d33a4
 * Created by thangnguyen on 12/24/17.
 */
class BadgeTabLayout : TabLayout {
    private val mTabBuilders = SparseArray<Builder>()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun with(position: Int): Builder {
        val tab = getTabAt(position)
        return with(tab)
    }

    /**
     * Apply a builder for this tab.
     *
     * @param tab for which we create a new builder or retrieve its builder if existed.
     * @return the required Builder.
     */
    fun with(tab: TabLayout.Tab?): Builder {
        if (tab == null) {
            throw IllegalArgumentException("Tab must not be null")
        }

        var builder: Builder? = mTabBuilders.get(tab.position)
        if (builder == null) {
            builder = Builder(this, tab)
            mTabBuilders.put(tab.position, builder)
        }

        return builder
    }

    class Builder
    /**
     * This construct take a TabLayout parent to have its context and other attributes sets. And
     * the tab whose icon will be updated.
     *
     * @param parent
     * @param tab
     */
     constructor(parent: TabLayout, internal val mTab: TabLayout.Tab) {

        @Nullable internal val mView: View?
        internal val mContext: Context
        @Nullable internal var mBadgeTextView: TextView? = null
        @Nullable internal var mIconView: ImageView? = null
        internal var mIconDrawable: Drawable? = null
        internal var mIconColorFilter: Int? = null
        internal var mBadgeCount = Integer.MIN_VALUE

        internal var mHasBadge = false

        init {
            this.mContext = parent.context
            // initialize current tab's custom view.
            if (mTab.customView != null) {
                this.mView = mTab.customView
            } else {
                this.mView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.tab_custom_icon, parent, false)
            }

            if (mView != null) {
                this.mIconView = mView!!.findViewById(R.id.tab_icon) as ImageView
                this.mBadgeTextView = mView!!.findViewById(R.id.tab_badge)
            }

            if (this.mBadgeTextView != null) {
                this.mHasBadge = mBadgeTextView!!.visibility == View.VISIBLE
                try {
                    this.mBadgeCount = Integer.parseInt(mBadgeTextView!!.text.toString())
                } catch (er: NumberFormatException) {
                    er.printStackTrace()
                    this.mBadgeCount = INVALID_NUMBER
                }

            }

            if (this.mIconView != null) {
                mIconDrawable = mIconView!!.getDrawable()
            }
        }

        /**
         * The related Tab is about to have a badge
         *
         * @return this builder
         */
        fun hasBadge(): Builder {
            mHasBadge = true
            return this
        }

        /**
         * The related Tab is not about to have a badge
         *
         * @return this builder
         */
        fun noBadge(): Builder {
            mHasBadge = false
            return this
        }

        /**
         * Dynamically set the availability of tab's badge
         *
         * @param hasBadge
         * @return this builder
         */
        // This method is used for DEBUG purpose only
        /*hide*/
        fun badge(hasBadge: Boolean): Builder {
            mHasBadge = hasBadge
            return this
        }

        /**
         * Set icon custom drawable by Resource ID;
         *
         * @param drawableRes
         * @return this builder
         */
        fun icon(drawableRes: Int): Builder {
            mIconDrawable = getDrawableCompat(mContext, drawableRes)
            return this
        }

        /**
         * Set icon custom drawable by Drawable Object
         *
         * @param drawable
         * @return this builder
         */
        fun icon(drawable: Drawable): Builder {
            mIconDrawable = drawable
            return this
        }

        /**
         * Set drawable color. Use this when user wants to change drawable's color filter
         *
         * @param color
         * @return this builder
         */
        fun iconColor(color: Int?): Builder {
            mIconColorFilter = color
            return this
        }

        /**
         * increase current badge by 1
         *
         * @return this builder
         */
        fun increase(): Builder {
            mBadgeCount = if (mBadgeTextView == null)
                INVALID_NUMBER
            else
                Integer.parseInt(mBadgeTextView!!.text.toString()) + 1
            return this
        }

        /**
         * decrease current badge by 1
         *
         * @return
         */
        fun decrease(): Builder {
            mBadgeCount = if (mBadgeTextView == null)
                INVALID_NUMBER
            else
                Integer.parseInt(mBadgeTextView!!.text.toString()) - 1
            return this
        }

        /**
         * set badge count
         *
         * @param count expected badge number
         * @return this builder
         */
        fun badgeCount(count: Int): Builder {
            mBadgeCount = count
            return this
        }

        /**
         * Build the current Tab icon's custom view
         */
        fun build() {
            if (mView == null) {
                return
            }

            // update badge counter
            if (mBadgeTextView != null) {
                mBadgeTextView!!.text = formatBadgeNumber(mBadgeCount)

                if (mHasBadge) {
                    mBadgeTextView!!.visibility = View.VISIBLE
                } else {
                    // set to View#INVISIBLE to not screw up the layout
                    mBadgeTextView!!.visibility = View.INVISIBLE
                }
            }

            // update icon drawable
            if (mIconView != null && mIconDrawable != null) {
                mIconView!!.setImageDrawable(mIconDrawable!!.mutate())
                // be careful if you modify this. make sure your result matches your expectation.
                if (mIconColorFilter != null) {
                    mIconDrawable!!.setColorFilter(mIconColorFilter!!, PorterDuff.Mode.MULTIPLY)
                }
            }

            mTab.setCustomView(mView)
        }

        companion object {

            /**
             * This badge widget must not support this value.
             */
            private val INVALID_NUMBER = Integer.MIN_VALUE
        }
    }

    companion object {

        private fun getDrawableCompat(context: Context, drawableRes: Int): Drawable? {
            var drawable: Drawable? = null
            try {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getResources().getDrawable(drawableRes)
                } else {
                    drawable = context.getResources().getDrawable(drawableRes, context.getTheme())
                }
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            }

            return drawable
        }

        /**
         * This format must follow User's badge policy.
         *
         * @param value of current badge
         * @return corresponding badge number. TextView need to be passed by a String/CharSequence
         */
        private fun formatBadgeNumber(value: Int): String {
            if (value < 0) {
                return "-" + formatBadgeNumber(-value)
            }

            return if (value <= 10) {
                // equivalent to String#valueOf(int);
                Integer.toString(value)
            } else "10+"

            // my own policy
        }
    }
}
