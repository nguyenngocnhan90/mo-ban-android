package com.moban.flow.cointransfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moban.R
import com.moban.enum.GACategory
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_coin_transfer.*
import kotlinx.android.synthetic.main.nav.view.*

class CoinTransferActivity : BaseMvpActivity<ICoinTransferView, ICoinTransferPresenter>(), ICoinTransferView {
    override var presenter: ICoinTransferPresenter = CoinTransferPresenterIml()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CoinTransferActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_coin_transfer
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Coin Transfer", GACategory.HOME)
        val newFragment = CoinTransferFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.lixi_frame_container, newFragment).commit()

        lixi_nav.nav_tvTitle.text = getString(R.string.tab_trans_coin)
        lixi_nav.nav_imgBack.setOnClickListener {
            finish()
        }
    }
}
