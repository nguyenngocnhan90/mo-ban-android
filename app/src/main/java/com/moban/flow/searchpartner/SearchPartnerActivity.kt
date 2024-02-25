package com.moban.flow.searchpartner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.moban.R
import com.moban.adapter.user.PartnerAdapter
import com.moban.enum.GACategory
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.handler.PartnerItemHandler
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_search_partner.*
import kotlinx.android.synthetic.main.nav.view.*

class SearchPartnerActivity : BaseMvpActivity<ISearchPartnerView, ISearchPartnerPresenter>(), ISearchPartnerView {
    override var presenter: ISearchPartnerPresenter = SearchPartnerPresenterIml()

    private var partnerAdapter = PartnerAdapter()


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SearchPartnerActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search_partner
    }

    override fun initialize(savedInstanceState: Bundle?) {
        search_partner_nav.nav_tvTitle.text = getString(R.string.search_partner_title)
        search_partner_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        initRecycleView()

        initSearchInput()
        setGAScreenName("Search Partner", GACategory.SEARCH)
    }

    private fun initSearchInput() {
        search_partner_ed_search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {
                val keyword = editable.toString()
                val isEmpty = keyword.isEmpty()
                if (isEmpty) {
                    partnerAdapter.removeAllUsers()
                } else {
                    presenter.searchPartner(keyword)
                }

                search_partner_btn_clear.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(keyword: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        search_partner_btn_clear.setOnClickListener {
            search_partner_btn_clear.visibility = View.INVISIBLE
            KeyboardUtil.hideKeyboard(this)
            search_partner_ed_search.setText("")
            partnerAdapter.removeAllUsers()
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)

        search_partner_recycleView.layoutManager = layoutManager
        search_partner_recycleView.adapter = partnerAdapter

        partnerAdapter.listener = object : PartnerItemHandler {
            override fun onClicked(user: User) {
                AccountDetailActivity.start(this@SearchPartnerActivity, user.id)
            }
        }
    }

    override fun bindingSearchPartner(users: List<User>) {
        partnerAdapter.setUsers(users)
    }
}
