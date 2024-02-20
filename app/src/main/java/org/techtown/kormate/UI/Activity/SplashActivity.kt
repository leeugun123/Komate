package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.kakao.sdk.common.util.Utility
import org.techtown.kormate.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var keyHash = Utility.getKeyHash(this)
        Log.e("GlobalApplication", "$keyHash")

        Handler().postDelayed({
            moveToLoginActivity()
        }, SPLASH_DURATION)

    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val SPLASH_DURATION : Long = 1500 // 1.5초
    }

}