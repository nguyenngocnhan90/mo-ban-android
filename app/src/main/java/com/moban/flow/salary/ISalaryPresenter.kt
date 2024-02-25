package com.moban.flow.salary

import com.moban.mvp.BaseMvpPresenter

interface ISalaryPresenter: BaseMvpPresenter<ISalaryView> {
    fun getSalary(userId: Int, month: Int, year: Int)
}
