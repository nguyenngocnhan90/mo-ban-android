package com.moban.flow.linkmart.list

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.mvp.BaseMvpPresenter
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinkmartListPresenterIml: BaseMvpPresenter<ILinkmartListView>, ILinkmartListPresenter {
    private var view: ILinkmartListView? = null
    private var context: Context? = null

    override fun attachView(view: ILinkmartListView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadNewestDeal(page: Int, cateId: Int?) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.newest(category =  cateId, page = page)?.enqueue(object : Callback<List<Linkmart>> {
            override fun onFailure(call: Call<List<Linkmart>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Linkmart>>?, response: Response<List<Linkmart>>?) {
                response?.body()?.let {
                    view?.bindingAllProducts(it)
                }
            }
        })
    }

    override fun loadPopularDeal(page: Int, cateId: Int?) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.popular(category =  cateId, page = page)?.enqueue(object : Callback<List<Linkmart>> {
            override fun onFailure(call: Call<List<Linkmart>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Linkmart>>?, response: Response<List<Linkmart>>?) {
                response?.body()?.let {
                    view?.bindingAllProducts(it)
                }
            }
        })
    }

    override fun loadSpecialDeal(page: Int, cateId: Int?) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.specials(category =  cateId, page = page)?.enqueue(object : Callback<List<Linkmart>> {
            override fun onFailure(call: Call<List<Linkmart>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Linkmart>>?, response: Response<List<Linkmart>>?) {
                response?.body()?.let {
                    view?.bindingAllProducts(it)
                }
            }
        })
    }

    override fun getCategory(cateId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.category(cateId)?.enqueue(object : Callback<LinkmartCategory> {
            override fun onFailure(call: Call<LinkmartCategory>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<LinkmartCategory>?, response: Response<LinkmartCategory>?) {
                response?.body()?.let {
                    view?.bindingCategory(it)
                }
            }
        })
    }
}
