package com.moban.flow.cointransfer

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.moban.LHApplication
import com.moban.R
import com.moban.flow.cointransfer.history.CoinHistoryActivity
import com.moban.handler.PartnerItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.user.User
import com.moban.model.param.user.TransferCoinParam
import com.moban.model.response.LoginResponse
import com.moban.model.response.SimpleResponse
import com.moban.network.service.UserService
import com.moban.util.DialogSearchPartner
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import kotlinx.android.synthetic.main.fragment_coin_transfer.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoinTransferFragment : Fragment() {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private lateinit var fragmentActivity: FragmentActivity
    private var receiver: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coin_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentActivity = this.activity!!

        initKeyBoardListener()

        lixi_tv_receiver.setOnClickListener {
            val dialog = DialogSearchPartner(context!!, fragmentActivity, object: PartnerItemHandler {
                override fun onClicked(user: User) {
                    receiver = user
                    lixi_tv_receiver.text = user.name
                }
            })

            dialog.showDialog()
        }

        lixi_btn_send_lixi.setOnClickListener {
            sendLixi()
        }

        lixi_btn_history_lixi.setOnClickListener {
            lixiHistory()
        }
    }

    private fun lixiHistory() {
        CoinHistoryActivity.start(fragmentActivity)
    }

    private fun initKeyBoardListener() {
        KeyboardVisibilityEvent.setEventListener(fragmentActivity) { isOpen ->
            if (lixi_keyboard_area != null) {
                lixi_keyboard_area.visibility = if (isOpen) View.VISIBLE else View.GONE
                if (isOpen) {
                    Handler().postDelayed({
                        if (lixi_keyboard_area != null) {
                            lixi_scrollView.scrollTo(0, lixi_keyboard_area.height)
                        }
                    }, 100)
                }
            }
        }
    }

    private fun sendLixi() {
        if (receiver == null) {
            DialogUtil.showErrorDialog(fragmentActivity,
                    getString(R.string.error_input_lixi),
                    getString(R.string.error_empty_receiver_lixi),
                    getString(R.string.ok), null)
            return
        }

        val lhcCoin = lixi_et_num_lhc.text.toString().toIntOrNull()
        if (lhcCoin == null || lhcCoin == 0) {
            DialogUtil.showErrorDialog(fragmentActivity,
                    getString(R.string.error_input_lixi),
                    getString(R.string.error_input_lixi_lhc),
                    getString(R.string.ok), null)
            return
        }

        val param = TransferCoinParam()
        param.to_User_Id = receiver!!.id
        param.linkCoin = lhcCoin
        param.message = lixi_ed_note.text.toString()
        param.description = lixi_ed_note.text.toString()

        if (!NetworkUtil.hasConnection(context!!)) {
            DialogUtil.showErrorDialog(fragmentActivity,
                    getString(R.string.network_no_internet),
                    getString(R.string.network_check_and_try_again),
                    getString(R.string.ok), null)
            return
        }

        lixi_progressbar.visibility = View.VISIBLE


        userService?.transferCoin(param)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                lixi_progressbar.visibility = View.GONE
                showError("")
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                lixi_progressbar.visibility = View.GONE
                if (response == null) {
                    showError("")
                    return
                }
                if (response.isSuccessful) {
                    response.body()?.let {
                        if(it.success) {
                            DialogUtil.showInfoDialog(fragmentActivity,
                                    getString(R.string.congrats),
                                    if (response.body()?.data.isNullOrEmpty()) "" else it.data!!,
                                    getString(R.string.ok), null)

                            getProfile()
                        } else  {
                            showError(if (it.error.isNullOrEmpty()) "" else it.error!!)
                        }
                    }

                } else {
                    showError(if (response.body()?.error.isNullOrEmpty()) "" else response.body()?.error!!)
                }
            }
        })
    }

    private fun getProfile() {
        userService?.profile()?.enqueue(object : Callback<LoginResponse>{
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                response.body()?.data?.let { user ->
                    LocalStorage.saveUser(user)
                }
            }
        })
    }

    private fun showError(message: String) {
        DialogUtil.showErrorDialog(fragmentActivity,
                getString(R.string.error_lixi),
                message, getString(R.string.ok),
                null)
    }

}
