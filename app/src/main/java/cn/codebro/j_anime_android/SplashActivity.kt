package cn.codebro.j_anime_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.lifecycle.lifecycleScope
import cn.codebro.j_anime_android.core.BaseView
import cn.codebro.j_anime_android.databinding.ActivitySplashBinding
import cn.codebro.j_anime_android.state.ApplicationState
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseView() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupApplication()
        setupAnimation()
    }

    private fun setupApplication() {
        lifecycleScope.launch {
            launch {
                applicationDataStore.data.collect {
                    if (it[APPLICATION_STATE_PK].isNullOrBlank())
                        startActivityByClass(ServerSettingActivity::class.java)
                    else
                        it[APPLICATION_STATE_PK]?.let { appPf ->
                            // 从Preferences中获取应用状态
                            val appState =
                                JAnimeApplication.gson.fromJson(appPf, ApplicationState::class.java)
                            JAnimeApplication.applicationState.refresh(appState)

                            // 应用状态存在，初始化api管理器
                            JAnimeApplication.apiManager!!.setup(ApplicationState.server!!)

                            // 如果token存在，跳转到主布局检验是否能获取数据，不能再跳转到登录界面进行登录操作
                            if (appState.token.isNullOrBlank())
                                startActivityByClass(MainActivity::class.java)
                            else
                                startActivityByClass(MainActivity::class.java)

                        }
                }
            }
        }
    }

    private fun startActivityByClass(target: Class<out Activity>) {
        runOnUiThread {
            val intent = Intent(this@SplashActivity, target)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupAnimation() {
        val rotateAnimation = RotateAnimation(
            0F,
            360F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = Animation.INFINITE

        binding.circleOne.animation = rotateAnimation
        binding.circleTwo.animation = rotateAnimation
        binding.circleThree.animation = rotateAnimation
    }
}