package cn.codebro.j_anime_android.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.codebro.j_anime_android.LoginActivity
import kotlin.system.exitProcess

abstract class BaseView : AppCompatActivity(), IView {
    private val requestCode: Int = 1
    private val requiredPermissions = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )

    private fun checkPermission() {
        try {
            val permission = ActivityCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, requiredPermissions, requestCode
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exitApp() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    private fun backToDesktop() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        checkPermission()
    }

    override fun onBackPressed() {
        if (this is LoginActivity) {
            backToDesktop()
        }
        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            if (grantResults.first() == PackageManager.PERMISSION_DENIED) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("是否拒绝权限")
                    .setMessage("该权限是请求网络图片需要缓存到手机存储空间中，如果拒绝赋予该权限，将无法使用相关功能。")
                builder.setPositiveButton("赋予") { dialog, _ ->
                    dialog.dismiss()
                    checkPermission()
                }
                builder.setNegativeButton("拒绝") { dialog, _ ->
                    dialog.dismiss()
                    exitApp()
                }
                builder.create().show()
            }
        }
    }

    override fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@BaseView, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun notLogin() {
        if (this !is LoginActivity) {
            runOnUiThread {
                val intent = Intent(this@BaseView, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        Log.d("BaseActivity", "Intent to another Activity")
    }

    override fun getContext(): Context? = this
}


