package com.moban.flow.reward.manager

import android.content.Context
import com.moban.LHApplication
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import retrofit2.Retrofit

class RewardPartnerManagerPresenterIml: BaseMvpPresenter<IRewardPartnerManagerView>, IRewardPartnerManagerPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    val userService = retrofit?.create(UserService::class.java)

    private var view: IRewardPartnerManagerView? = null
    private var context: Context? = null

    override fun attachView(view: IRewardPartnerManagerView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }
}
