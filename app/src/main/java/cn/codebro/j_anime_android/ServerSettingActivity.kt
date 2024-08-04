package cn.codebro.j_anime_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import cn.codebro.j_anime_android.core.BaseView
import cn.codebro.j_anime_android.core.IView
import cn.codebro.j_anime_android.databinding.ActivityServerSettingBinding
import cn.codebro.j_anime_android.net.ApiCallback
import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.CaptchaVO
import kotlinx.coroutines.launch
import retrofit2.Call

interface ServerSettingView : IView {
    fun testConnSuccess()
    fun testConnFailure()
}

class ServerSettingActivity : BaseView(), ServerSettingView {

    private lateinit var binding: ActivityServerSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.serverAddressEditText.setText("http://ani.live1024.cn:5000/")

        binding.saveServerAddressButton.setOnClickListener {
            val address = binding.serverAddressEditText.text.trim().toString()

            JAnimeApplication.apiManager?.let { manager ->

                manager.setup(address)

                val userService = manager.userService()
                userService.captcha(System.currentTimeMillis().toString())
                    .enqueue(TestConnCallback(this))

            }

        }
    }

    override fun testConnSuccess() {
        showToast("连接成功")
        lifecycleScope.launch {
            launch {
                JAnimeApplication.applicationState.server =
                    binding.serverAddressEditText.text.trim().toString()
                applicationDataStore.edit {
                    Log.d("setServerSetting", JAnimeApplication.applicationState.toString())
                    it[APPLICATION_STATE_PK] =
                        JAnimeApplication.gson.toJson(JAnimeApplication.applicationState)

                    runOnUiThread {
                        val intent = Intent(this@ServerSettingActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }
            }
        }

    }

    override fun testConnFailure() {
        showToast("服务器连接失败，请检查服务器地址是否正确")
    }


    inner class TestConnCallback(override val view: ServerSettingView) :
        ApiCallback<CaptchaVO>(view) {
        override fun onResponseSuccess(response: ApiResponse<CaptchaVO>) {
            view.testConnSuccess()
        }

        override fun onFailure(call: Call<ApiResponse<CaptchaVO>>, t: Throwable) {
            view.testConnFailure()
        }
    }

}