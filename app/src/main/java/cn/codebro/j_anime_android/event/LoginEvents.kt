package cn.codebro.j_anime_android.event

import cn.codebro.j_anime_android.core.event.Event

class NotLoginEvent(private val param: Map<String, Any>?) : Event {
    override fun getName(): String = "NOT_LOGIN"
    override fun getParam(): Map<String, Any> = if (param.isNullOrEmpty()) HashMap() else param
}

class LoginSuccessEvent(private val param: Map<String, Any>?) : Event {
    override fun getName(): String = "LOGIN_SUCCESS"
    override fun getParam(): Map<String, Any> = if (param.isNullOrEmpty()) HashMap() else param
}

