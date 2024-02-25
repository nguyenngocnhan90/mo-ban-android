package com.moban.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.user.PartnerAdapter
import com.moban.handler.PartnerItemHandler
import com.moban.model.data.user.User
import com.moban.model.response.user.ListUsersResponse
import com.moban.network.service.LeadService
import kotlinx.android.synthetic.main.dialog_partner.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DialogSearchPartner(private var context: Context, private var activity: Activity, private var listener: PartnerItemHandler?) {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val leadService = retrofit?.create(LeadService::class.java)

    private var partnerAdapter = PartnerAdapter()
    val dialog = Dialog(context)

    fun showDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_partner, null, false)
        initSearchInput(view)
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

        view.dialog_search_partner_btn_close.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun initSearchInput(view: View) {
        view.dialog_search_partner_ed_search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {
                val keyword = editable.toString()
                val isEmpty = keyword.isEmpty()
                if (isEmpty) {
                    partnerAdapter.removeAllUsers()
                } else {
                    searchPartner(keyword)
                }

                view.dialog_search_partner_ed_search.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(keyword: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        view.dialog_search_partner_btn_clear.setOnClickListener {
            view.dialog_search_partner_btn_clear.visibility = View.INVISIBLE
            KeyboardUtil.hideKeyboard(activity)
            view.dialog_search_partner_ed_search.setText("")
            partnerAdapter.removeAllUsers()
        }
    }

    private fun initRecycleView(view: View) {
        val layoutManager = LinearLayoutManager(context)

        view.dialog_partner_recycleView.layoutManager = layoutManager
        view.dialog_partner_recycleView.adapter = partnerAdapter

        partnerAdapter.listener = object : PartnerItemHandler {
            override fun onClicked(user: User) {
                listener?.onClicked(user)
                dialog.dismiss()
            }
        }
    }

    private fun searchPartner(keyword: String) {
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        leadService?.users(keyword)?.enqueue(object : Callback<ListUsersResponse> {
            override fun onFailure(call: Call<ListUsersResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListUsersResponse>?, response: Response<ListUsersResponse>?) {
                response?.body()?.data?.let {
                    partnerAdapter.setUsers(it.list)
                }
            }
        })
    }

}
