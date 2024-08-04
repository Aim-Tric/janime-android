package cn.codebro.j_anime_android.net

import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.CaptchaVO
import cn.codebro.j_anime_android.pojo.LoginDTO
import cn.codebro.j_anime_android.pojo.LoginUserVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("api/system/captcha")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun captcha(@Query("timestamp") timestamp : String): Call<ApiResponse<CaptchaVO>>

    @POST("api/system/login")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun login(@Body data: LoginDTO): Call<ApiResponse<String>>

    @GET("api/system/loginInfo")
    fun loginInfo(): Call<ApiResponse<LoginUserVO>>

    @POST("api/system/logout")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun logout(): Call<ApiResponse<Unit>>

}