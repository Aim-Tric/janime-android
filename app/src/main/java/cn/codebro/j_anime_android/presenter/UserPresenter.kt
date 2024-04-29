package cn.codebro.j_anime_android.presenter

import androidx.datastore.preferences.core.edit
import cn.codebro.j_anime_android.JAnimeApplication
import cn.codebro.j_anime_android.LOGIN_USER_PK
import cn.codebro.j_anime_android.LoginView
import cn.codebro.j_anime_android.loginUserDataStore
import cn.codebro.j_anime_android.net.ApiCallback
import cn.codebro.j_anime_android.net.UserService
import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.CaptchaDTO
import cn.codebro.j_anime_android.pojo.LoginDTO
import cn.codebro.j_anime_android.pojo.LoginUserVO
import kotlinx.coroutines.runBlocking
import retrofit2.Call

class UserPresenter(private val view: LoginView) {
    private val userService: UserService = JAnimeApplication.apiManager!!.userService()

    fun loadCaptcha() {
        userService.captcha(CaptchaDTO("LOGIN"))
            .enqueue(CaptchaCallback(view))
    }

    fun login(loginDTO: LoginDTO) {
        userService.login(loginDTO).enqueue(LoginCallback(view))
    }

    fun refreshUserInfo() {
        userService.loginInfo().enqueue(GetLoginInfoCallback(view))
    }

    inner class CaptchaCallback(override val view: LoginView) :
        ApiCallback<String>(view) {
        override fun onResponseSuccess(response: ApiResponse<String>) {
            response.data?.let { view.setCaptcha(it) }
        }
    }

    inner class LoginCallback(override val view: LoginView) :
        ApiCallback<String>(view) {

        override fun onResponseSuccess(response: ApiResponse<String>) {
            refreshUserInfo()
        }

        override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
            t.printStackTrace()
            view.loginFail("登录异常")
        }
    }

    inner class GetLoginInfoCallback(override val view: LoginView) :
        ApiCallback<LoginUserVO>(view) {

        override fun onResponseSuccess(response: ApiResponse<LoginUserVO>) {
            runBlocking {
                view.getContext()!!.loginUserDataStore.edit {
                    it[LOGIN_USER_PK] = JAnimeApplication.gson.toJson(it)
                    view.loginSuccess()
                }
            }
        }

    }

}
