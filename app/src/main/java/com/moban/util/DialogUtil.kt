package com.moban.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.*
import android.webkit.WebViewClient
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.secondary.AttributeAdapter
import com.moban.adapter.secondary.BasicConstantAdapter
import com.moban.adapter.secondary.HouseTypeAdapter
import com.moban.adapter.secondary.ProjectBaseAdapter
import com.moban.model.data.media.Photo
import com.moban.model.data.secondary.constant.Attribute
import com.moban.model.data.secondary.constant.BasicConstantType
import com.moban.model.data.secondary.constant.HouseType
import com.moban.model.data.secondary.constant.ProjectBase
import kotlinx.android.synthetic.main.alert.view.*
import kotlinx.android.synthetic.main.alert_sad.view.*
import kotlinx.android.synthetic.main.alert_welcome.view.*
import kotlinx.android.synthetic.main.dialog_text.view.*
import kotlinx.android.synthetic.main.dialog_webview.view.*

/**
 * Created by neal on 3/21/18.
 */
class DialogUtil {
    companion object {
        fun showWelcomeDialog(context: Context): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert_welcome, null, false)

            val fontMedium = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_medium))
            val fontRegular = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_regular))

            view.alert_welcome_tv_title.typeface = fontMedium
            view.alert_welcome_tv_content.typeface = fontRegular
            view.alert_welcome_btn_close.typeface = fontMedium

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)

            view.alert_welcome_btn_close.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            return dialog
        }

        fun showErrorDialog(context: Context, title: String, content: String, buttonOkTitle: String, action: View.OnClickListener?): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert_sad, null, false)

            val fontMedium = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_medium))
            val fontRegular = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_regular))

            view.alert_sad_tv_title.typeface = fontMedium
            view.alert_sad_tv_content.typeface = fontRegular
            view.alert_sad_btn_close.typeface = fontMedium

            view.alert_sad_tv_title.text = title.toUpperCase()
            view.alert_sad_tv_content.text = content
            view.alert_sad_btn_close.text = buttonOkTitle

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)

            if (action == null) {
                view.alert_sad_btn_close.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                action.let {
                    view.alert_sad_btn_close.setOnClickListener(it)
                }
            }

            dialog.show()
            return dialog
        }

        fun showErrorDialog(context: Context, title: String, content: String, buttonOkTitle: String, buttonRejectTitle: String, action: View.OnClickListener?, actionReject: View.OnClickListener?): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert_sad, null, false)

            val fontMedium = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_medium))
            val fontRegular = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_regular))

            view.alert_sad_btn_close.visibility = View.GONE
            view.alert_sad_view_confirm.visibility = View.VISIBLE

            view.alert_sad_tv_title.typeface = fontMedium
            view.alert_sad_tv_content.typeface = fontRegular
            view.alert_sad_btn_confirm.typeface = fontMedium
            view.alert_sad_btn_reject.typeface = fontMedium

            view.alert_sad_tv_title.text = title.toUpperCase()
            view.alert_sad_tv_content.text = content
            view.alert_sad_btn_confirm.text = buttonOkTitle
            view.alert_sad_btn_reject.text = buttonRejectTitle


            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)

            if (action == null) {
                view.alert_sad_btn_close.setOnClickListener {
                    dialog.dismiss()
                }

                view.alert_sad_btn_confirm.setOnClickListener {
                    dialog.dismiss()
                }

            } else {
                action.let {
                    view.alert_sad_btn_close.setOnClickListener(it)
                    view.alert_sad_btn_confirm.setOnClickListener(it)
                }
            }

            if (actionReject == null) {
                view.alert_sad_btn_reject.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                actionReject.let {
                    view.alert_sad_btn_reject.setOnClickListener(it)
                }
            }

            dialog.show()
            return dialog
        }

        fun showInfoDialog(context: Context, title: String, content: String, buttonOkTitle: String, action: View.OnClickListener?): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert, null, false)

            val fontBold = Font.boldTypeface(context)
            val fontRegular = Font.regularTypeface(context)

            view.alert_view_confirm.visibility = View.GONE

            view.alert_tv_title.typeface = fontBold
            view.alert_tv_content.typeface = fontRegular
            view.alert_btn_ok.typeface = fontBold

            view.alert_tv_title.text = title.toUpperCase()
            view.alert_tv_content.text = content
            view.alert_btn_ok.text = buttonOkTitle

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)

            if (action == null) {
                view.alert_btn_ok.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                action.let {
                    view.alert_btn_ok.setOnClickListener(it)
                }
            }

            dialog.show()
            return dialog
        }

        fun showPopupDialog(context: Context, content: String, photo: Photo?, action: View.OnClickListener?): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert, null, false)

            val fontBold = Font.boldTypeface(context)
            val fontRegular = Font.regularTypeface(context)

            view.alert_image_logo.visibility = View.GONE
            view.alert_btn_ok.visibility = View.GONE

            val photoWidth = photo?.photo_Size?.width ?: 0
            val photoHeight = photo?.photo_Size?.height ?: 0
            val photoLink = photo?.photo_Link ?: ""
            val isNoPhoto = photo == null || photoLink.isEmpty() || photoHeight == 0 || photoWidth == 0
            view.alert_image_view.visibility = if (isNoPhoto) View.GONE else View.VISIBLE
            if (!isNoPhoto) {
                LHPicasso.loadImage(photoLink, view.alert_image_view)
            }

            view.alert_tv_title.typeface = fontBold
            view.alert_tv_content.typeface = fontRegular
            view.alert_btn_ok.typeface = fontBold

            view.alert_tv_title.visibility = View.GONE
            view.alert_tv_content.text = content
            view.alert_tv_content.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            view.alert_btn_confirm.setText(R.string.view)

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)

            action?.let {
                view.alert_btn_confirm.setOnClickListener(it)
            }

            view.alert_btn_reject.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

            if (!isNoPhoto) {
//                val layoutParam = view.alert_image_view.layoutParams
//                layoutParam.height = layoutParam.width * photoHeight / photoWidth
//                view.alert_image_view.layoutParams = layoutParam
            }

            return dialog
        }

        fun showConfirmDialog(context: Context, isSad: Boolean = false, title: String, content: String,
                              buttonConfirmTitle: String, buttonRejectTitle: String,
                              actionConfirm: View.OnClickListener?, actionReject: View.OnClickListener?): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.alert, null, false)

            val fontMedium = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_medium))
            val fontRegular = Typeface.createFromAsset(context.assets,
                    context.resources.getString(R.string.font_regular))

            view.alert_btn_ok.visibility = View.GONE

            view.alert_tv_title.typeface = fontMedium
            view.alert_tv_content.typeface = fontRegular
            view.alert_btn_ok.typeface = fontMedium
            view.alert_btn_confirm.typeface = fontMedium
            view.alert_btn_reject.typeface = fontMedium

            view.alert_tv_title.text = title.toUpperCase()
            view.alert_tv_content.text = content
            view.alert_btn_confirm.text = buttonConfirmTitle
            view.alert_btn_reject.text = buttonRejectTitle

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)

            if (actionConfirm == null) {
                view.alert_btn_confirm.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                actionConfirm.let {
                    view.alert_btn_confirm.setOnClickListener(it)
                }
            }

            if (actionReject == null) {
                view.alert_btn_reject.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                actionReject.let {
                    view.alert_btn_reject.setOnClickListener(it)
                }
            }

            dialog.show()
            return dialog
        }

        fun showConfirmDialog(context: Context, isSad: Boolean = false, title: Int, content: Int,
                              buttonConfirmTitle: Int, buttonRejectTitle: Int,
                              actionConfirm: View.OnClickListener?, actionReject: View.OnClickListener?): Dialog {
            return showConfirmDialog(context, isSad,
                    context.getString(title),
                    context.getString(content),
                    context.getString(buttonConfirmTitle),
                    context.getString(buttonRejectTitle),
                    actionConfirm, actionReject)
        }

        fun showNetworkError(context: Context, tryAction: View.OnClickListener?): Dialog {
            var okTitle = R.string.ok
            if (tryAction != null) {
                okTitle = R.string.network_try_again
            }

            return showErrorDialog(context,
                    context.getString(R.string.network_no_internet),
                    context.getString(R.string.network_check_and_try_again),
                    context.getString(okTitle),
                    tryAction)
        }

        fun showLoginRequiredDialog(context: Context, action: View.OnClickListener): Dialog {
            return showConfirmDialog(context,
                    false,
                    "",
                    context.getString(R.string.login_required_message),
                    context.getString(R.string.signIn),
                    context.getString(R.string.skip),
                    action, null)
        }

        @SuppressLint("SetJavaScriptEnabled")
        fun showWebviewDialog(context: Context, title: String, url: String): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_webview, null, false)

            view.dialog_web_tvTitle.text = title
            view.dialog_web_container.webViewClient = WebViewClient()
            view.dialog_web_container.settings.javaScriptEnabled = true
            view.dialog_web_container.loadUrl(url)
            val dialog = Dialog(context, R.style.FullScreenDialogStyle)
            dialog.setContentView(view)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
            dialog.show()

            view.dialog_web_nav_close.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun showProjectMenu(context: Context, list: List<ProjectBase>, handler: ProjectBaseAdapter.ProjectBaseItemHandler): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null, false)
            view.dialog_text_tv_title.text = context.getText(R.string.select_project_title)
            val adapter = ProjectBaseAdapter()
            val linearLayoutManager = LinearLayoutManager(context)
            view.dialog_text_recycleView.layoutManager = linearLayoutManager
            view.dialog_text_recycleView.adapter = adapter
            adapter.setDataList(list)
            adapter.listener = handler

            val dialog = showDialogWithView(context, view)

            view.dialog_text_btn_close.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        private fun showDialogWithView(context: Context, view: View): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(view)

            val widthDialog = Device.getScreenWidth() * 0.9
            val heightDialog = Device.getScreenHeight() * 0.9
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = widthDialog.toInt()
            lp.height = heightDialog.toInt()
            lp.gravity = Gravity.CENTER
            dialog.window?.attributes = lp
            dialog.show()
            return dialog
        }

        fun showHouseTypeMenu(context: Context, list: List<HouseType>, handler: HouseTypeAdapter.HouseTypeItemHandler): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null, false)
            view.dialog_text_tv_title.text = context.getText(R.string.select_type_bds_title)
            val adapter = HouseTypeAdapter()
            val linearLayoutManager = LinearLayoutManager(context)
            view.dialog_text_recycleView.layoutManager = linearLayoutManager
            view.dialog_text_recycleView.adapter = adapter
            adapter.setDataList(list)
            adapter.listener = handler

            val dialog = showDialogWithView(context, view)

            view.dialog_text_btn_close.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun showBasicConstantMenu(context: Context, title: String, list: List<BasicConstantType>, handler: BasicConstantAdapter.BasicConstantTypeItemHandler): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null, false)
            view.dialog_text_tv_title.text = title
            val adapter = BasicConstantAdapter()
            val linearLayoutManager = LinearLayoutManager(context)
            view.dialog_text_recycleView.layoutManager = linearLayoutManager
            view.dialog_text_recycleView.adapter = adapter
            adapter.setDataList(list)
            adapter.listener = handler

            val dialog = showDialogWithView(context, view)

            view.dialog_text_btn_close.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun showAttributeMenu(context: Context, title: String, list: List<Attribute>, handler: AttributeAdapter.AttributeItemHandler): Dialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null, false)
            view.dialog_text_tv_title.text = title
            val adapter = AttributeAdapter()
            val linearLayoutManager = LinearLayoutManager(context)
            view.dialog_text_recycleView.layoutManager = linearLayoutManager
            view.dialog_text_recycleView.adapter = adapter
            adapter.setDataList(list)
            adapter.listener = handler

            val dialog = showDialogWithView(context, view)

            view.dialog_text_btn_close.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }
    }
}
