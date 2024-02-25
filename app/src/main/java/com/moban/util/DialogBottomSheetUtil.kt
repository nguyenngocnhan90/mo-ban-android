package com.moban.util

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moban.R
import com.moban.adapter.feed.TopicAdapter
import com.moban.adapter.linkmart.LinkmartCateAdapter
import com.moban.adapter.project.ItemMoneyBottomAdapter
import com.moban.enum.ApproveStatus
import com.moban.enum.DealStatus
import com.moban.enum.LinkmartSortType
import com.moban.enum.SortType
import com.moban.handler.*
import com.moban.model.data.deal.Deal
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.post.Topic
import kotlinx.android.synthetic.main.dialog_bottom_document.view.*
import kotlinx.android.synthetic.main.dialog_bottom_edit_deal.view.*
import kotlinx.android.synthetic.main.dialog_bottom_feed.view.*
import kotlinx.android.synthetic.main.dialog_bottom_meu.view.*
import kotlinx.android.synthetic.main.dialog_bottom_photo_secondary_project.view.*
import kotlinx.android.synthetic.main.dialog_bottom_project.view.*
import kotlinx.android.synthetic.main.dialog_bottom_quick_menu.view.*
import kotlinx.android.synthetic.main.dialog_bottom_reservation.view.*
import kotlinx.android.synthetic.main.dialog_bottom_secondary_project.view.*
import kotlinx.android.synthetic.main.dialog_bottom_sort_linkmart.view.*

/**
 * Created by H. Len Vo on 8/24/18.
 */
class DialogBottomSheetUtil {
    companion object {
        fun showDialogFeedOption(
            isFollowed: Boolean,
            isOwner: Boolean,
            context: Context,
            handler: FeedMenuHandler
        ): BottomSheetDialog {
            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_bottom_feed, null, false)
            view.feed_menu_tv_notify.text =
                if (isFollowed) context.getText(R.string.feed_notify_off) else
                    context.getText(R.string.feed_notify)

            view.feed_menu_view_notify.setOnClickListener {
                handler.onClickedNotify()
            }

            view.feed_menu_view_edit.visibility = if (isOwner) View.VISIBLE else View.GONE
            view.feed_menu_view_delete.visibility = if (isOwner) View.VISIBLE else View.GONE

            view.feed_menu_view_edit.setOnClickListener {
                handler.onClickedEdit()
            }
            view.feed_menu_view_delete.setOnClickListener {
                handler.onClickedDelete()
            }

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogFeedOption(
            context: Context,
            sortType: SortType,
            handler: ProjectMenuHandler
        ): BottomSheetDialog {
            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_bottom_project, null, false)
            val sortItemArr = arrayOf(
                view.sort_project_hot, view.sort_project_latest, view.sort_project_reward,
                view.sort_project_asc, view.sort_project_desc
            )
            val iconItemArr = arrayOf(
                view.sort_project_img_hot,
                view.sort_project_img_latest,
                view.sort_project_img_reward,
                view.sort_project_img_asc,
                view.sort_project_img_desc
            )
            val textItemArr = arrayOf(
                view.sort_project_tv_hot, view.sort_project_tv_latest, view.sort_project_tv_reward,
                view.sort_project_tv_asc, view.sort_project_tv_desc
            )
            val checkItemArr = arrayOf(
                view.sort_project_img_check_hot,
                view.sort_project_img_check_latest,
                view.sort_project_img_check_reward,
                view.sort_project_img_check_asc,
                view.sort_project_img_check_desc
            )

            sortItemArr.forEachIndexed { idx, _ ->
                val selected = SortType.getSortTypeProject()[idx] == sortType
                val tintColor = Util.getColor(
                    context, if (selected) R.color.colorAccent
                    else R.color.color_black
                )
                ImageViewCompat.setImageTintList(
                    iconItemArr[idx],
                    ColorStateList.valueOf(tintColor)
                )
                textItemArr[idx].setTextColor(tintColor)
                checkItemArr[idx].visibility = if (selected) View.VISIBLE else View.GONE
            }

            sortItemArr.forEachIndexed { index, linearLayout ->
                linearLayout.setOnClickListener {
                    val selectedType = SortType.getSortTypeProject()[index]
                    handler.onClickSort(selectedType)
                }
            }

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogCenterQuickAction(
            context: Context,
            handler: MainQuickButtonHandle
        ): BottomSheetDialog {
            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_bottom_quick_menu, null, false)
            val sortItemArr = arrayOf(
                view.main_center_request_project,
                view.main_center_call_support,
                view.main_center_refer_partner
            )

            sortItemArr.forEachIndexed { index, linearLayout ->
                linearLayout.setOnClickListener {
                    when (index) {
                        0 -> {
                            handler.requestProduct()
                        }
                        1 -> {
                            handler.callSupport()
                        }
                        2 -> {
                            handler.referPartner()
                        }
                    }
                }
            }

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogSortLinkmartMenu(
            context: Context,
            sortType: LinkmartSortType,
            handler: LinkmartSortMenuHandler
        ): BottomSheetDialog {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_bottom_sort_linkmart, null, false)
            val sortItemArr = arrayOf(
                view.sort_linkmart_sale,
                view.sort_linkmart_popular,
                view.sort_linkmart_newest
            )
            val iconItemArr = arrayOf(
                view.sort_linkmart_img_sale,
                view.sort_linkmart_img_popular,
                view.sort_linkmart_img_newest
            )
            val textItemArr = arrayOf(
                view.sort_linkmart_tv_sale,
                view.sort_linkmart_tv_popular,
                view.sort_linkmart_tv_newest
            )
            val checkItemArr = arrayOf(
                view.sort_linkmart_img_check_sale, view.sort_linkmart_img_check_popular,
                view.sort_linkmart_img_check_newest
            )

            sortItemArr.forEachIndexed { idx, _ ->
                val selected = LinkmartSortType.getSortType()[idx] == sortType
                val tintColor = Util.getColor(
                    context, if (selected)
                        R.color.color_menu_selected else R.color.color_black
                )
                ImageViewCompat.setImageTintList(
                    iconItemArr[idx],
                    ColorStateList.valueOf(tintColor)
                )
                textItemArr[idx].setTextColor(tintColor)
                checkItemArr[idx].visibility = if (selected) View.VISIBLE else View.GONE
            }

            sortItemArr.forEachIndexed { index, linearLayout ->
                linearLayout.setOnClickListener {
                    val selectedType = LinkmartSortType.getSortType()[index]
                    handler.onClickSort(selectedType)
                }
            }

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogLinkmartCategoryMenu(
            context: Context, current: LinkmartCategory,
            categories: List<LinkmartCategory>, handler: LinkmartCateHandler
        ): Dialog {
            val adapter = LinkmartCateAdapter(current)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_meu, null, false)
            view.dialog_bottom_menu_recycleview.adapter = adapter
            view.dialog_bottom_menu_recycleview.layoutManager = LinearLayoutManager(context)
            adapter.listener = handler
            adapter.setListCategories(categories)

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogListMoney(
            context: Context, current: Int?,
            values: List<Int>, handler: ItemMoneyBottomHandler
        ): Dialog {
            val adapter = ItemMoneyBottomAdapter(current)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_meu, null, false)
            view.dialog_bottom_menu_recycleview.adapter = adapter
            view.dialog_bottom_menu_recycleview.layoutManager = LinearLayoutManager(context)
            adapter.listener = handler
            adapter.setListValues(values)

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogListTopics(
            context: Context, current: Topic?,
            topics: List<Topic>, handler: TopicItemHandler
        ): Dialog {
            val adapter = TopicAdapter(current)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_meu, null, false)
            view.dialog_bottom_menu_recycleview.adapter = adapter
            view.dialog_bottom_menu_recycleview.layoutManager = LinearLayoutManager(context)
            adapter.listener = handler
            adapter.setListTopics(topics)

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogReservationMenu(
            context: Context,
            deal: Deal,
            copyAllowed: Boolean = true,
            handler: ReservationMenuHandler
        ): BottomSheetDialog {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_bottom_reservation, null, false)
            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            view.reservation_menu_copy.visibility =
                if (deal.getApproveStatus() != ApproveStatus.Denied && copyAllowed)
                    View.VISIBLE else View.GONE

            val dealStatus = deal.getDealStatus()
            val isCanceled = dealStatus == DealStatus.CANCELED

            view.reservation_menu_resubmit.visibility = if (isCanceled) View.VISIBLE else View.GONE

            dialog.show()
            view.reservation_menu_tv_copy.setOnClickListener {
                dialog.dismiss()
                handler.onCopy()
            }

            view.reservation_menu_cancel.visibility =
                if (deal.canCancel()) View.VISIBLE else View.GONE
            view.reservation_menu_cancel.setOnClickListener {
                dialog.dismiss()
                handler.onCancel()
            }

            view.reservation_menu_refund.visibility =
                if (deal.canRefund()) View.VISIBLE else View.GONE
            view.reservation_menu_refund.setOnClickListener {
                dialog.dismiss()
                handler.onRefundNumber()
            }

            view.reservation_menu_resubmit.setOnClickListener {
                dialog.dismiss()
                handler.onReSubmit()
            }

            view.reservation_menu_change_to_coc.visibility =
                if (deal.canDeposit()) View.VISIBLE else View.GONE
            view.reservation_menu_change_to_coc.setOnClickListener {
                dialog.dismiss()
                handler.onChangeToDeposit()
            }

            view.reservation_menu_change_to_contract.visibility =
                if (deal.canMakeContract()) View.VISIBLE else View.GONE
            view.reservation_menu_change_to_contract.setOnClickListener {
                dialog.dismiss()
                handler.onChangeToContract()
            }

            return dialog
        }

        fun showDialogDocumentMenu(
            context: Context,
            showDownload: Boolean,
            handler: DocumentBottomMenuItemHandler
        ): BottomSheetDialog {
            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_bottom_document, null, false)
            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)

            view.document_btn_download.visibility = if (showDownload) View.VISIBLE else View.GONE

            dialog.show()
            view.document_btn_download.setOnClickListener {
                dialog.dismiss()
                handler.onDownload()
            }

            view.document_btn_share.setOnClickListener {
                dialog.dismiss()
                handler.onShare()
            }
            return dialog
        }

        fun showDialogListActionSecondary(
            context: Context,
            handler: ItemSecondaryMenuBottomHandler
        ): Dialog {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_bottom_secondary_project, null, false)
            view.dialog_create_new_secondary_project.setOnClickListener {
                handler.onAddNewProject()
            }

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogListActionPhotoSecondary(
            context: Context,
            isMainPhoto: Boolean,
            handler: PhotoHorizontalBottomMenuHandler
        ): Dialog {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_bottom_photo_secondary_project, null, false)
            view.dialog_make_main_photo.visibility = if (isMainPhoto) View.GONE else View.VISIBLE
            view.dialog_make_main_photo.setOnClickListener {
                handler.onSetMainPhoto()
            }

            view.dialog_delete_photo.setOnClickListener {
                handler.onDeletePhoto()
            }

            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialogMenuEditDeal(context: Context, handler: DealBottomMenuEditHandler): Dialog {
            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_bottom_edit_deal, null, false)
            val dialog = BottomSheetDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
            view.deal_menu_edit.setOnClickListener {
                dialog.dismiss()
                handler.onEdit()
            }
            return dialog
        }
    }
}
