package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import org.techtown.kormate.Constant.FirebasePathConstant.USER_INTEL_PATH
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Model.UserKakaoIntel.userId
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.UI.Activity.UserInfoRegister.NationActivity
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val kakaoViewModel by lazy { ViewModelProvider(this)[KakaoViewModel::class.java] }
    private val myIntelViewModel by lazy {ViewModelProvider(this)[MyIntelViewModel::class.java]}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->

            if (error != null) {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                kakaoLoginSuccess()
            }

        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }


                }
            } else if (token != null) {
                kakaoLoginSuccess()
            }


        }


        binding.kakaoLogin.setOnClickListener {

            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this,callback = callback)
            }
            else{
                UserApiClient.instance.loginWithKakaoAccount(this,callback = callback)
            }

            kakaoLoginSuccess()

        }


    }

    private fun kakaoLoginSuccess(){

        bindingKakaoInfo()
        Toast.makeText(this, KAKAO_ACCESS_SUCCESS, Toast.LENGTH_SHORT).show()
        myIntelViewModel.checkDataExist()

        myIntelViewModel.dataExistLiveData.observe(this){exist ->
            startActivity(decideIntent(exist))
            finish()
        }

    }


    private fun bindingKakaoInfo() {

        lifecycleScope.launch(Dispatchers.Main){
            kakaoViewModel.loadUserData()
        }

        kakaoViewModel.userKakaoIntel.observe(this){
            userNickName = it.userNickName
            userProfileImg = it.userProfileImg
            userId = it.userId
        }

    }




    private fun decideIntent(exist : Boolean) = if(exist){
        Intent(this@LoginActivity, MainActivity::class.java)
    } else
        Intent(this@LoginActivity, NationActivity::class.java)


    companion object{
        private const val KAKAO_ACCESS_DENIED = "카카오 로그인 실패"
        private const val KAKAO_ACCESS_SUCCESS = "카카오 로그인"

        private const val OTHER_ERROR = "기타 에러"
        private const val INVALID_ERROR = "유효하지 않은 앱"
        private const val ACCESS_DENIED = "접근이 거부 됨(동의 취소)"
    }

}
