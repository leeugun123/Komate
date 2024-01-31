package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.techtown.kormate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private lateinit var userKakaoAccount : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        kakaoLoginProcess()

        val callback : (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                lifecycleScope.launch(Dispatchers.Main) { handleKakaoLoginError(error) }
            } else if (token != null) {
                kakaoLoginSuccess()
            }
        }

        binding.kakaoLogin.setOnClickListener {
            lifecycleScope.launch {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }
        }


    }

    private fun kakaoLoginProcess() {

        UserApiClient.instance.me { user: User?, _ ->
            userKakaoAccount = user!!
        }

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            } else if (tokenInfo != null) {
                kakaoLoginSuccess()
            }
        }

    }


    private suspend fun checkDataExistence(userId: String): Boolean = withContext(Dispatchers.IO) {

        val firebaseInstance = FirebaseDatabase.getInstance()
        val reference = firebaseInstance.reference.child("usersIntel").child(userId)

        return@withContext try {
            reference.get().await().exists()
        } catch (e: Exception) { false }

    }

    private fun kakaoLoginSuccess(){
        Toast.makeText(this, "카카오 로그인", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.Main) {
            moveNextActivity()
        }
    }

    private suspend fun checkId(userId: String) = checkDataExistence(userId)

    private suspend fun handleKakaoLoginError(error: Throwable) = withContext(Dispatchers.Main) {
        when {
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                Toast.makeText(this@LoginActivity, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                Toast.makeText(this@LoginActivity, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
            }
            // Add other cases as needed
            else -> {
                Toast.makeText(this@LoginActivity, "기타 에러", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private suspend fun moveNextActivity() {
        startActivity(decideActivity())
        finish()
    }

    private suspend fun decideActivity() = if(checkId(userKakaoAccount!!.id.toString())){
        Intent(this@LoginActivity, MainActivity::class.java)
    } else
        Intent(this@LoginActivity, NationActivity::class.java)


}
