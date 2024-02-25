package com.moban.flow.salary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.moban.R
import com.moban.adapter.user.PersonalRevenueAdapter
import com.moban.adapter.user.HeaderAdapter
import com.moban.enum.GACategory
import com.moban.enum.Role
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.RecyclerItemListener
import com.moban.helper.LocalStorage
import com.moban.model.data.salary.BasicSalary
import com.moban.model.data.salary.GroupRevenue
import com.moban.model.data.salary.PersonalRevenue
import com.moban.model.data.salary.Salary
import com.moban.model.data.salary.SalaryMonth
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DateUtil
import com.moban.util.NumberUtil
import com.moban.util.PdfUtil
import kotlinx.android.synthetic.main.activity_salary.*
import kotlinx.android.synthetic.main.nav.view.*
import kotlin.collections.ArrayList

class SalaryActivity : BaseMvpActivity<ISalaryView, ISalaryPresenter>(), ISalaryView {

    companion object {
        const val BUNDLE_KEY_USER = "BUNDLE_KEY_USER"

        fun start(context: Context) {
            val intent = Intent(context, SalaryActivity::class.java)
            context.startActivity(intent)
        }

        fun start(context: Context, user: User) {
            val intent = Intent(context, SalaryActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USER, user)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override var presenter: ISalaryPresenter = SalaryPresenterIml()

    private var monthAdapter: HeaderAdapter? = null
    private var personalRevenueAdapter: PersonalRevenueAdapter? = null

    private var currentMonth: Int = 0
    private var currentYear: Int = 0

    private var listMonths: MutableList<SalaryMonth> = ArrayList()
    private var user: User? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_salary
    }

    override fun initialize(savedInstanceState: Bundle?) {
        salary_nav.nav_tvTitle.text = getString(R.string.salary)
        salary_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        user = if (intent.hasExtra(BUNDLE_KEY_USER)) {
            bundle?.getSerializable(BUNDLE_KEY_USER) as User
        } else {
            LocalStorage.user()
        }

        getCurrentTime()
        initRecyclerMonth()
        initPersonalRevenueRecyclerView()
        setGAScreenName("Salary", GACategory.ACCOUNT)
    }

    private fun getCurrentTime() {
        val current = DateUtil.currentTimeSeconds()
        currentMonth = DateUtil.dateStringFromSeconds(current, "M").toInt()
        currentYear = DateUtil.dateStringFromSeconds(current, "yyyy").toInt()

        didSelectMonth(SalaryMonth(currentMonth, currentYear))
    }

    private fun initMonthList(): List<String> {
        listMonths = ArrayList()

        for (i in 0..5) {
            var month = currentMonth - i
            var year = currentYear

            if (month <= 0) {
                month += 12
                year -= 1
            }

            val salaryMonth = SalaryMonth(month, year)
            listMonths.add(salaryMonth)
        }

        listMonths = listMonths.reversed().toMutableList()

        val listHeaderMonthTexts: MutableList<String> = ArrayList()
        listMonths.forEachIndexed { _, salaryMonth ->
            listHeaderMonthTexts.add(("Tháng " + (salaryMonth.month.toString())).toUpperCase())
        }

        return listHeaderMonthTexts
    }

    private fun initRecyclerMonth() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        salary_recycler_month.layoutManager = layoutManager

        monthAdapter = HeaderAdapter()
        salary_recycler_month.adapter = monthAdapter

        monthAdapter?.recyclerItemListener = object : RecyclerItemListener {
            override fun onClick(view: View, index: Int) {
                if (index < listMonths.count()) {
                    monthAdapter?.currentIndex = index
                    monthAdapter?.notifyDataSetChanged()

                    didSelectMonth(listMonths[index])
                }
            }
        }

        val texts = initMonthList()
        monthAdapter?.currentIndex = texts.count() - 1
        monthAdapter?.texts = texts

        salary_recycler_month.scrollToPosition(texts.count() - 1)
    }

    private fun initPersonalRevenueRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        salary_recycler_personal_revenue.layoutManager = layoutManager

        personalRevenueAdapter = PersonalRevenueAdapter()
        salary_recycler_personal_revenue.adapter = personalRevenueAdapter
    }

    private fun didSelectMonth(salaryMonth: SalaryMonth) {
        val month = salaryMonth.month
        val year = salaryMonth.year

        // Show previous month
        val previousMonth = if (month == 1) 12 else month - 1

        val revenueTitle = "Tổng Doanh Thu \n2 Tháng Liền Kề (Tháng " + previousMonth.toString() +
                " + " + month.toString() + ")"
        salary_tv_2month_revenue_title.text = revenueTitle

        val revenueGroup = "Doanh Thu Nhóm Tháng $month"
        salary_tv_group_revenue_title.text = revenueGroup

        val currentMonth = "Tổng thu nhập tạm tính"
        salary_tv_current_month.text = currentMonth

        salary_tv_total_salary.visibility = View.GONE
        salary_loading_view.visibility = View.VISIBLE
        salary_view_detail.visibility = View.GONE

        user?.let {
            presenter.getSalary(it.id, month, year)
        }

    }

    /**
     * View override
     */

    override fun onFillSalary(salary: Salary) {
        salary_loading_view.visibility = View.GONE
        salary_view_detail.visibility = View.VISIBLE

        salary_tv_total_salary.visibility = View.VISIBLE
        salary_tv_total_salary.text = NumberUtil.formatPrice(salary.total_revenue.toDouble(), this)

        salary_view_detail.visibility = if (isDirector()) View.GONE else View.VISIBLE

        if (!isDirector()) {
            fillTotalRevenue(salary)
            fillPersonalRevenue(salary.personal_revenue)

            salary_view_support_fee.visibility = if (salary.salary != null) View.VISIBLE else View.GONE
            salary_view_group_management.visibility = if (salary.group_revenue != null) View.VISIBLE else View.GONE

            val indexGroupManager = 1 + (if (salary.salary != null) 1 else 0)
            val titleGroupManager = indexGroupManager.toString() + ". " + getString(R.string.group_management_fee)
            salary_tv_group_management.text = titleGroupManager


            salary.salary?.let {
                fillBasicSalary(it)
            }

            salary.group_revenue?.let {
                fillGroupRevenue(it)
            }
        }

        val indexRevenue = 1 + (if (salary.salary != null) 1 else 0) +
                (if (salary.group_revenue != null) 1 else 0)
        val titleRevenue = indexRevenue.toString() + ". " + getString(R.string.personal_revenue)
        salary_tv_personal_revenue.text = titleRevenue


        salary_view_policy.setOnClickListener {
            salary.salary?.let {
                if (!it.policy_Link.isNullOrBlank()) {
                    WebViewActivity.start(this, getString(R.string.policy),
                            PdfUtil.generatePdfDocURL(it.policy_Link))
                }
            }
        }
    }

    /**
     * Fill salary
     */

    private fun isDirector(): Boolean {
        val user = LocalStorage.user()
        user?.let {
            return user.role_Id == Role.giamDoc.value
        }

        return false
    }

    private fun fillBasicSalary(salary: BasicSalary) {
        salary_tv_2month_revenue.text = NumberUtil.formatPrice(salary.revenue_2_Last_Month.toDouble(), this)
        salary_tv_coefficients.text = salary.coefficients.toString()
        salary_tv_hard_salary.text = NumberUtil.formatPrice(salary.hard_Wage.toDouble(), this)
    }

    private fun fillGroupRevenue(revenue: GroupRevenue) {
        salary_tv_group_revenue.text = NumberUtil.formatPrice(revenue.group_Revenue.toDouble(), this)
        salary_tv_group_commission.text = NumberUtil.formatPrice(revenue.leader_Commission.toDouble(), this)

        val commissionRate = "Phí Quản Lý (" + revenue.leader_Commission_Rate.toString() + "%)"
        salary_tv_group_commission_rate.text = commissionRate
    }

    private fun fillTotalRevenue(salary: Salary) {
        salary_tv_total_revenue.text = NumberUtil.formatPrice(salary.subtotal_revenue.toDouble(), this)
        salary_tv_total_tax.text = NumberUtil.formatPrice(salary.tax.toDouble(), this)
        salary_tv_final_salary.text = NumberUtil.formatPrice(salary.total_revenue.toDouble(), this)
    }

    private fun fillPersonalRevenue(revenues: List<PersonalRevenue>) {
        personalRevenueAdapter?.personalRevenues = revenues
        salary_layout_personal_revenue.visibility = if (revenues.isEmpty()) View.GONE else View.VISIBLE
    }
}
