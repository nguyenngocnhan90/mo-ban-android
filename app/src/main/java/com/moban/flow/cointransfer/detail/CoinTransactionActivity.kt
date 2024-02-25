package com.moban.flow.cointransfer.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moban.R
import com.moban.enum.GACategory
import com.moban.model.data.user.LhcTransaction
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DateUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_coin_transaction.*
import kotlinx.android.synthetic.main.nav.view.*

/**
 * Created by lenvo on 3/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class CoinTransactionActivity : BaseMvpActivity<ICoinTransactionView, ICoinTransactionPresenter>(), ICoinTransactionView {
    override var presenter: ICoinTransactionPresenter = CoinTransactionPresenterIml()
    private var transaction: LhcTransaction? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_coin_transaction
    }

    companion object {
        private const val BUNDLE_KEY_COIN_TRANSACTION = "BUNDLE_KEY_COIN_TRANSACTION"
        private const val BUNDLE_KEY_COIN_TRANSACTION_ID = "BUNDLE_KEY_COIN_TRANSACTION_ID"

        fun start(context: Context, transactionId: Int) {
            val intent = Intent(context, CoinTransactionActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_COIN_TRANSACTION_ID, transactionId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, transaction: LhcTransaction) {
            val intent = Intent(context, CoinTransactionActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_COIN_TRANSACTION, transaction)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Coin transaction detail", GACategory.ACCOUNT)
        val bundle = intent.extras

        coin_transaction_nav.nav_tvTitle.text = getText(R.string.coin_transaction_title)
        coin_transaction_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        transaction = bundle?.getSerializable(BUNDLE_KEY_COIN_TRANSACTION) as LhcTransaction

        fillDataInformation()
    }

    private fun fillDataInformation() {
        transaction?.let { transaction ->
            val time = DateUtil.dateStringFromSeconds(transaction.created_Date.toLong(), DateUtil.DF_FULL_DATE_STRING)
            coin_transaction_tv_info_time_value.text = time

            coin_transaction_tv_info_source_value.text = transaction.description
            val coin = transaction.linkCoin
            val sign = if (coin > 0) "+" else ""
            val coinValue = "$sign$coin Điểm"
            coin_transaction_tv_coin.text = coinValue
            coin_transaction_tv_coin.setTextColor(Util.getColor(this, if (coin > 0) R.color.colorPrimary else if (coin < 0) R.color.color_red else R.color.color_black_50))
        }
    }

    override fun bindingTransaction(transaction: LhcTransaction) {
    }
}
