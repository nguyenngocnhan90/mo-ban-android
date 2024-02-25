package com.moban.flow.reward

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.user.LevelResponse
import com.moban.model.response.user.ListLevelGiftResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.LevelService
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RewardPresenterIml : BaseMvpPresenter<IRewardView>, IRewardPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)
    private val levelService = retrofit?.create(LevelService::class.java)

    private var view: IRewardView? = null
    private var context: Context? = null

    override fun attachView(view: IRewardView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getLevel(levelId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        levelService?.get(levelId)?.enqueue(object : Callback<LevelResponse> {
            override fun onFailure(call: Call<LevelResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<LevelResponse>?, response: Response<LevelResponse>?) {
                response?.body()?.let { it ->
                    it.data?.let {
                        view?.fillLevel(it)
                    }
                }
            }
        })
    }

    override fun getAvailableGifts(userId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.gifts(userId)?.enqueue(object : Callback<ListLevelGiftResponse> {
            override fun onFailure(call: Call<ListLevelGiftResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListLevelGiftResponse>?, response: Response<ListLevelGiftResponse>?) {
                response?.body()?.let { it ->
                    it.data?.let {
                        view?.fillGifts(it.list)
                    }
                }
            }
        })
    }

    override fun getLevelGifts(levelId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        levelService?.gifts(levelId)?.enqueue(object : Callback<ListLevelGiftResponse> {
            override fun onFailure(call: Call<ListLevelGiftResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListLevelGiftResponse>?, response: Response<ListLevelGiftResponse>?) {
                response?.body()?.let { it ->
                    it.data?.let {
                        view?.fillNextGifts(it.list)
                    }
                }
            }
        })
    }
}
