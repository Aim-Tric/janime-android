package cn.codebro.j_anime_android

import android.content.Intent
import android.os.Bundle
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import cn.codebro.j_anime_android.core.BaseView
import cn.codebro.j_anime_android.core.IView
import cn.codebro.j_anime_android.databinding.ActivityServerSettingBinding
import cn.codebro.j_anime_android.net.ApiCallback
import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.CaptchaDTO
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

        binding.saveServerAddressButton.setOnClickListener {
            val address = binding.serverAddressEditText.text.trim().toString()

            JAnimeApplication.apiManager?.let { manager ->

                manager.setup(address)

                val userService = manager.userService()
                userService.captcha(CaptchaDTO("LOGIN"))
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
        ApiCallback<String>(view) {
        override fun onResponseSuccess(response: ApiResponse<String>) {
            view.testConnSuccess()
        }

        override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
            view.testConnFailure()
        }
    }

}