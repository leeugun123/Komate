package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.UI.Activity.UserInfoRegister.NationActivity
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val kakaoViewModel : KakaoViewModel by viewModels()
    private val myIntelViewModel : MyIntelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        kakaoAutoLogin()

        binding.kakaoLogin.setOnClickListener {
            checkUserApiClient()
        }

    }

    private fun kakaoAutoLogin() {

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->

            if (error != null)
                errorMessageToast(KAKAO_LOGIN_FAILED)
            else if (tokenInfo != null)
                kakaoLoginSuccess()
        }

    }

    private fun errorMessageToast(errorCause : String) {
        Toast.makeText(this, errorCause , Toast.LENGTH_SHORT).show()
    }

    private fun checkUserApiClient() {

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

            if (error != null) {

                when (error.toString()) {

                    AuthErrorCause.AccessDenied.toString() -> {
                        errorMessageToast(ACCESS_DENIED)
                    }
                    AuthErrorCause.InvalidClient.toString() -> {
                        errorMessageToast(INVALID_ERROR)
                    }
                    AuthErrorCause.InvalidGrant.toString() -> {
                        errorMessageToast(CAN_NOT_AUTHENTICATION)
                    }
                    AuthErrorCause.InvalidRequest.toString() -> {
                        errorMessageToast(REQUEST_PARAMETER_ERROR)
                    }
                    AuthErrorCause.InvalidScope.toString() -> {
                        errorMessageToast(INVALID_SCOPE_ID)
                    }
                    AuthErrorCause.Misconfigured.toString() -> {
                        errorMessageToast(SETTING_NOT_RIGHT)
                    }
                    AuthErrorCause.ServerError.toString() -> {
                        errorMessageToast(SERVER_INTERNAL_ERROR)
                    }
                    AuthErrorCause.Unauthorized.toString() -> {
                        errorMessageToast(NOT_HAVE_REQUEST_PERMISSION)
                    }
                    else -> { // Unknown
                        errorMessageToast(OTHER_ERROR)
                    }

                }

            } else if (token != null) {
                kakaoLoginSuccess()
            }

        }

        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this))
            UserApiClient.instance.loginWithKakaoTalk(this,callback = callback)
        else
            UserApiClient.instance.loginWithKakaoAccount(this,callback = callback)

    }

    private fun kakaoLoginSuccess(){

        bindingKakaoInfo()
        errorMessageToast(KAKAO_ACCESS_SUCCESS)
        checkMyIntelData()

    }

    private fun checkMyIntelData() {

        myIntelViewModel.checkDataExist()

        myIntelViewModel.dataExistLiveData.observe(this){ exist ->
            startActivity(decideIntent(exist))
            finish()
        }

    }


    private fun bindingKakaoInfo() {

        loadKakaoIntel()
        kakaoViewModelObserve()

    }

    private fun kakaoViewModelObserve() {
        kakaoViewModel.kakaoIntelDownloadSuccess.observe(this){ success ->
            if(!success)
                Toast.makeText(this,KAKAO_DATA_BINDING_FAILED,Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadKakaoIntel() {
        kakaoViewModel.loadUserData()
    }


    private fun decideIntent(exist : Boolean) = if(exist){
        Intent(this@LoginActivity, MainActivity::class.java)
    } else
        Intent(this@LoginActivity, NationActivity::class.java)


    companion object{
        private const val KAKAO_LOGIN_FAILED = "카카오 로그인 실패"
        private const val KAKAO_ACCESS_SUCCESS = "카카오 로그인"
        private const val OTHER_ERROR = "기타 에러"
        private const val INVALID_ERROR = "유효하지 않은 앱"
        private const val ACCESS_DENIED = "접근이 거부 됨(동의 취소)"
        private const val REQUEST_PARAMETER_ERROR = "요청 파라미터 오류"
        private const val CAN_NOT_AUTHENTICATION = "인증 수단이 유효하지 않아 인증할 수 없는 상태"
        private const val INVALID_SCOPE_ID = "유효 하지 않은 scope ID"
        private const val SETTING_NOT_RIGHT = "설정이 올바르지 않음(android key hash)"
        private const val SERVER_INTERNAL_ERROR = "서버 내부 에러"
        private const val NOT_HAVE_REQUEST_PERMISSION = "앱이 요청 권한이 없음"
        private const val KAKAO_DATA_BINDING_FAILED = "카카오 데이터 바인딩 실패"
    }

}
