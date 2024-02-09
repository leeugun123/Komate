package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.techtown.kormate.FirebasePathConstant.USER_INTEL_PATH
import org.techtown.kormate.Model.UserKakaoIntel.userId
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.UI.Activity.UserInfoRegister.NationActivity
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val kakaoViewModel by lazy { ViewModelProvider(this)[KakaoViewModel::class.java] }

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

            lifecycleScope.launch(Dispatchers.IO) {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }

        }


    }

    private fun handleKakaoLoginError(error: Throwable): () -> Unit = {

        when {
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                Toast.makeText(this@LoginActivity, ACCESS_DENIED, Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                Toast.makeText(this@LoginActivity, INVALID_ERROR, Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this@LoginActivity, OTHER_ERROR , Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun kakaoLoginProcess() {

       bindingKakaoInfo()
        decideLogin()

    }

    private fun bindingKakaoInfo() {

        kakaoViewModel.loadUserData()

        kakaoViewModel.userId.observe(this) {
            userId = it.toString()
        }

        kakaoViewModel.userProfileImageUrl.observe(this){
            userProfileImg= it
        }

        kakaoViewModel.userName.observe(this){
            userNickName = it
        }
    }

    private fun decideLogin(){

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, KAKAO_ACCESS_DENIED, Toast.LENGTH_SHORT).show()
            } else if (tokenInfo != null) {
                kakaoLoginSuccess()
            }
        }

    }


    private fun kakaoLoginSuccess(){
        Toast.makeText(this, KAKAO_ACCESS_SUCCESS, Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.Main) {
            moveNextActivity()
        }
    }

    private suspend fun moveNextActivity() {
        startActivity(decideActivity())
        finish()
    }

    private suspend fun decideActivity() = if(checkId(userId.toString())){
        Intent(this@LoginActivity, MainActivity::class.java)
    } else
        Intent(this@LoginActivity, NationActivity::class.java)

    private suspend fun checkId(userId: String) = checkDataExistence(userId)

    private suspend fun checkDataExistence(userId: String) = withContext(Dispatchers.IO) {

        return@withContext try {
            FirebaseDatabase.getInstance().
            reference.child(USER_INTEL_PATH)
                .child(userId).get().await().exists()
        } catch (e: Exception) { false }

    }

    companion object{
        private const val KAKAO_ACCESS_DENIED = "카카오 로그인 실패"
        private const val KAKAO_ACCESS_SUCCESS = "카카오 로그인"

        private const val OTHER_ERROR = "기타 에러"
        private const val INVALID_ERROR = "유효하지 않은 앱"
        private const val ACCESS_DENIED = "접근이 거부 됨(동의 취소)"
    }

}
