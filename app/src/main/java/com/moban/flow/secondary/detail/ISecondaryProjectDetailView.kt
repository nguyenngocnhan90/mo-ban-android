package com.moban.flow.secondary.detail

import com.moban.model.data.secondary.SecondaryHouse
import com.moban.model.response.secondary.SecondaryHouseResponse
import com.moban.mvp.BaseMvpView

interface ISecondaryProjectDetailView: BaseMvpView {
    fun bindingProjectDetail(house: SecondaryHouse)
    fun bindingRequestContact(response: SecondaryHouseResponse)
    fun bindingRequestContactFailed(error: String)
}
