package com.moban.flow.salary

import com.moban.model.data.salary.Salary
import com.moban.mvp.BaseMvpView

interface ISalaryView : BaseMvpView {
    fun onFillSalary(salary: Salary)
}
