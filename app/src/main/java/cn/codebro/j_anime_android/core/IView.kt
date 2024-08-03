package cn.codebro.j_anime_android.core

import android.content.Context

interface IView {
    fun getContext(): Context?
    fun showToast(message: String)
    fun notLogin()
    fun fireEvent(name: String)
}