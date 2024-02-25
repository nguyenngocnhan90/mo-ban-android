package com.moban.util

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.user.UserMenuAdapter
import com.moban.extension.getString
import com.moban.model.response.user.ListUsersResponse
import com.moban.network.service.LeadService
import kotlinx.android.synthetic.main.dialog_text.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DialogSelectDirector(private var context: Context, private var listener: UserMenuAdapter.UserMenuItemHandler?) {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val leadService = retrofit?.create(LeadService::class.java)

    private var userAdapter = UserMenuAdapter()
    val dialog = Dialog(context)

    fun showDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null, false)
        view.dialog_text_tv_title.text = getString(context, R.string.director_hint)
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
        view.dialog_text_recycleView.adapter = userAdapter

        userAdapter.listener = listener
    }

    private fun loadDirectors() {
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        leadService?.directors()?.enqueue(object : Callback<ListUsersResponse> {
            override fun onFailure(call: Call<ListUsersResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListUsersResponse>?, response: Response<ListUsersResponse>?) {
                response?.body()?.data?.let {
                    userAdapter.setDataList(it.list)
                }
            }
        })
    }

}
