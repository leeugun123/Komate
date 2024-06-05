package org.techtown.kormate.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentLoginBinding
import org.techtown.kormate.presentation.ui.KakaoViewModel
import org.techtown.kormate.presentation.ui.home.myprofile.MyIntelViewModel
import org.techtown.kormate.presentation.util.base.BaseFragment
import org.techtown.kormate.presentation.util.extension.showToast

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_splash) {

    private val kakaoViewModel: KakaoViewModel by activityViewModels()
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
                requireContext().showToast(R.string.kakao_login_failed)
            else if (tokenInfo != null)
                handleKakaoLogin()
        }
    }

    private fun checkUserApiClient() {

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

            if (error != null) {

                when (error.toString()) {

                    AuthErrorCause.AccessDenied.toString() -> {
                        requireContext().showToast(R.string.access_denied)
                    }

                    AuthErrorCause.InvalidClient.toString() -> {
                        requireContext().showToast(R.string.invalid_error)
                    }

                    AuthErrorCause.InvalidGrant.toString() -> {
                        requireContext().showToast(R.string.can_not_authentication)
                    }

                    AuthErrorCause.InvalidRequest.toString() -> {
                        requireContext().showToast(R.string.request_parameter_error)
                    }

                    AuthErrorCause.InvalidScope.toString() -> {
                        requireContext().showToast(R.string.invalid_scope_id)
                    }

                    AuthErrorCause.Misconfigured.toString() -> {
                        requireContext().showToast(R.string.setting_not_right)
                    }

                    AuthErrorCause.ServerError.toString() -> {
                        requireContext().showToast(R.string.server_internal_error)
                    }

                    AuthErrorCause.Unauthorized.toString() -> {
                        requireContext().showToast(R.string.not_have_request_permission)
                    }

                    else -> {
                        requireContext().showToast(R.string.other_error)
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
        requireContext().showToast(R.string.kakao_access_success)
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
                requireContext().showToast(R.string.kakao_data_binding_failed)
        }
    }

    private fun loadKakaoIntel() {
        kakaoViewModel.loadUserData()
    }

    private fun decideIntent(exist: Boolean) {
        if (exist)
            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
        else
            findNavController().navigate(R.id.action_LoginFragment_to_SignUpFragment)
    }
}