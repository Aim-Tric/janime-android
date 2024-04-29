package cn.codebro.j_anime_android.presenter

import cn.codebro.j_anime_android.JAnimeApplication
import cn.codebro.j_anime_android.net.ApiCallback
import cn.codebro.j_anime_android.net.UserService
import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.ui.slideshow.UserInfoView

class UserInfoPresenter(private val view: UserInfoView) {
    private val userService: UserService = JAnimeApplication.apiManager!!.userService()

    fun logout() {
        userService.logout().enqueue(LogoutCallback(view))
    }

    inner class LogoutCallback(override val view: UserInfoView) : ApiCallback<Unit>(view) {
        override fun onResponseSuccess(response: ApiResponse<Unit>) {
            view.logout()
        }

    }
}