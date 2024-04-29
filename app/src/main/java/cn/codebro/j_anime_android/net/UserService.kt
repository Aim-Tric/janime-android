package cn.codebro.j_anime_android.net

import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.CaptchaDTO
import cn.codebro.j_anime_android.pojo.LoginDTO
import cn.codebro.j_anime_android.pojo.LoginUserVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {

    @POST("system/captcha")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun captcha(@Body codeType: CaptchaDTO): Call<ApiResponse<String>>

    @POST("system/login")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun login(@Body data: LoginDTO): Call<ApiResponse<String>>

    @GET("system/loginInfo")
    fun loginInfo(): Call<ApiResponse<LoginUserVO>>

    @POST("system/logout")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun logout(): Call<ApiResponse<Unit>>

}