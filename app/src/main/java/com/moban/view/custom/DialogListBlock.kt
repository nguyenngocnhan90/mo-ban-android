package com.moban.view.custom

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.moban.R
import com.moban.model.data.project.Block
import kotlinx.android.synthetic.main.dialog_list.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class DialogListBlock(context: Context, layoutInflater: LayoutInflater, listBlock: MutableList<Block>): AlertDialog.Builder(context) {
    init {
        val inflater = layoutInflater
        val convertView = inflater.inflate(R.layout.dialog_list, null) as View
        convertView.dialog_list_title.text = getContext().getText(R.string.project_detail_cart_select_block)
                .toString().toUpperCase()
        this.setView(convertView)
//        val adapter = ArrayAdapter(context, R.layout.item_dialog, names)
//        convertView.listView.setAdapter(adapter)
    }
}
