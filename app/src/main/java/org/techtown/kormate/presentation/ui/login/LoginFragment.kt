package org.techtown.kormate.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentLoginBinding
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.KakaoViewModel
import org.techtown.kormate.presentation.ui.home.myprofile.MyIntelViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_splash) {

    private val kakaoViewModel: KakaoViewModel by viewModels()
    private val myIntelViewModel: MyIntelViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        autoLoginKakao()
        initBinding()
    }

    private fun initBinding() {
        binding.onKakoLoginClick = ::checkUserApiClient
    }

    private fun autoLoginKakao() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null)
                errorMessageToast(requireContext().getString(R.string.kakao_login_failed))
            else if (tokenInfo != null)
                handleKakaoLogin()
        }
    }

    private fun errorMessageToast(errorCause: String) {
        Toast.makeText(requireContext(), errorCause, Toast.LENGTH_SHORT).show()
    }

    private fun checkUserApiClient() {

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

            if (error != null) {

                when (error.toString()) {

                    AuthErrorCause.AccessDenied.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.access_denied))
                    }

                    AuthErrorCause.InvalidClient.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.invalid_error))
                    }

                    AuthErrorCause.InvalidGrant.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.can_not_authentication))
                    }

                    AuthErrorCause.InvalidRequest.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.request_parameter_error))
                    }

                    AuthErrorCause.InvalidScope.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.invalid_scope_id))
                    }

                    AuthErrorCause.Misconfigured.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.setting_not_right))
                    }

                    AuthErrorCause.ServerError.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.server_internal_error))
                    }

                    AuthErrorCause.Unauthorized.toString() -> {
                        errorMessageToast(requireContext().getString(R.string.not_have_request_permission))
                    }

                    else -> {
                        errorMessageToast(requireContext().getString(R.string.other_error))
                    }
                }
            } else if (token != null) {
                handleKakaoLogin()
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext()))
            UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
        else
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
    }

    private fun handleKakaoLogin() {
        bindingKakaoInfo()
        errorMessageToast(requireContext().getString(R.string.kakao_access_success))
        checkMyIntelData()
    }

    private fun checkMyIntelData() {
        myIntelViewModel.checkDataExist()
        myIntelViewModel.dataExistLiveData.observe(viewLifecycleOwner) { exist ->
            decideIntent(exist)
        }
    }

    private fun bindingKakaoInfo() {
        loadKakaoIntel()
        observeKakaoViewModel()
    }

    private fun observeKakaoViewModel() {
        kakaoViewModel.kakaoIntelDownloadSuccess.observe(viewLifecycleOwner) { success ->
            if (!success)
                errorMessageToast(requireContext().getString(R.string.kakao_data_binding_failed))
        }
    }

    private fun loadKakaoIntel() {
        kakaoViewModel.loadUserData()
    }

    private fun decideIntent(exist: Boolean) {
        if (exist) {
            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
        } else {
            // TODO("정보 입력 화면으로 입력")
        }
    }
}