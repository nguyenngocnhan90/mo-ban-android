package com.moban.flow.signin

import com.moban.LHApplication
import com.moban.helper.LocalStorage
import com.moban.mvp.BaseMvpPresenter
import retrofit2.Retrofit
import com.moban.model.param.user.LoginParam
import com.moban.model.response.LoginResponse
import com.moban.network.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by neal on 3/1/18.
 */
class SignInPresenterIml : BaseMvpPresenter<ISignInView>, ISignInPresenter {

//    @Inject
    private var retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val service = retrofit?.create(UserService::class.java)
    protected var view: ISignInView? = null

    override fun attachView(view: ISignInView) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun handleSignIn() {
        val email = view?.getInputUsername() ?: ""
        val password = view?.getInputPassword() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
            view?.showInputNeededError()
            return
        }

        val param = LoginParam()
        param.username = email
        param.password = password

        service?.login(param)?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                showErrorLogin()
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response == null || !response.isSuccessful) {
                    showErrorLogin()
                    return
                }
                handleSignResult(response.body())
            }
        })
    }

    override fun loginAnonymous() {
        service?.loginAnonymous()?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showErrorLogin()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>?) {
                if (response == null || !response.isSuccessful) {
                    showErrorLogin()
                    return
                }
                handleSignResult(response.body())
            }
        })
    }

    fun handleSignResult(result: LoginResponse?) {
        // do some thing with the result

        result?.let { it ->
            if (it.success) {
                it.data?.let {
                    LocalStorage.saveUser(it)
                    LocalStorage.saveToken(it.token)

                    LHApplication.instance.lhCache.popup = it.popup
                    // Init net component for adding header token
                    LHApplication.instance.initNetComponent()
                    LHApplication.instance.initLinkmartNetComponent()

                    if (it.is_ForcedToChangePassword) {
                        view?.changePassword()
                        return
                    }

//                    if (!it.is_Phone_Verified) {
//                        view?.startVerifyPhone()
//                        return
//                    }

                    // start main screen
                    view?.startMainScreen()
                }
            }
            else {
                showErrorLogin()
            }
        }
    }

    private fun showErrorLogin() {
        val error = "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng kiểm tra và thử lại."
        val title = "Đăng nhập không được"

        view?.showLoginError(title, error)
    }

    private fun showErrorGetOTP() {
        val error = "Lấy mã xác nhận không thành công. Vui lòng kiểm tra và thử lại."
        val title = "Lấy mã xác nhận không được"

        view?.showLoginError(title, error)
    }
}
