package cn.codebro.j_anime_android.core

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.codebro.j_anime_android.LoginActivity


abstract class BaseFragment : Fragment(), IView {

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun fireEvent(name: String) {

    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun notLogin() {
        activity?.let {
            it.runOnUiThread {
                val intent = Intent(it, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
