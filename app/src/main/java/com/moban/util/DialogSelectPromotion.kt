package com.moban.util

import android.app.Dialog
import android.content.Context
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.PromotionAdapter
import com.moban.handler.PromotionDialogHandler
import com.moban.handler.PromotionItemHandler
import com.moban.model.data.deal.Promotion
import kotlinx.android.synthetic.main.dialog_list_promotions.view.*

class DialogSelectPromotion(private var context: Context,
                            private var promotions: List<Promotion>,
                            private var listener: PromotionDialogHandler?) {

    private var adapter = PromotionAdapter()
    val dialog = Dialog(context)

    fun showDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list_promotions, null, false)
        initRecycleView(view)

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

        view.dialog_promotions_btn_close.setOnClickListener {
            dialog.dismiss()
        }

        adapter.listener = object : PromotionItemHandler {
            override fun onSelected(promotion: Promotion) {
                listener?.onSelected(promotion)
                dialog.dismiss()
            }
        }
    }

    private fun initRecycleView(view: View) {
        val layoutManager = LinearLayoutManager(context)
        view.dialog_promotions_recycleView.layoutManager = layoutManager
        view.dialog_promotions_recycleView.adapter = adapter

        adapter.setPromotionList(promotions)
    }
}