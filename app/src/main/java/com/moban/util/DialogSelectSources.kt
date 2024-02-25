package com.moban.util

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.StringAdapter
import com.moban.extension.getString
import com.moban.handler.StringMenuItemHandler
import com.moban.model.response.ListStringDataResponse
import com.moban.network.service.LeadService
import kotlinx.android.synthetic.main.dialog_text.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DialogSelectSources(private var context: Context, private var listener: StringMenuItemHandler?) {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val leadService = retrofit?.create(LeadService::class.java)

    private var adapter = StringAdapter()
    val dialog = Dialog(context)

    fun showDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null, false)
        view.dialog_text_tv_title.text = getString(context, R.string.reference_select)
        initRecycleView(view)
        loadDirectors()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        val widthDialog = Device.getScreenWidth()*0.9
        val heightDialog = Device.getScreenHeight()*0.9
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = widthDialog.toInt()
        lp.height = heightDialog.toInt()
        lp.gravity = Gravity.CENTER

        dialog.window?.attributes = lp
        dialog.show()

        view.dialog_text_btn_close.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun initRecycleView(view: View) {
        val layoutManager = LinearLayoutManager(context)

        view.dialog_text_recycleView.layoutManager = layoutManager
        view.dialog_text_recycleView.adapter = adapter

        adapter.listener = listener
    }

    private fun loadDirectors() {
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        leadService?.sources()?.enqueue(object : Callback<ListStringDataResponse> {
            override fun onFailure(call: Call<ListStringDataResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListStringDataResponse>?, response: Response<ListStringDataResponse>?) {
                response?.body()?.data?.let {
                    adapter.setDataList(it.list)
                }
            }
        })
    }

}
