package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.salary.Salary

class SalaryResponse : BaseModel() {
    var success: Boolean = false
    var data: Salary? = null
}
