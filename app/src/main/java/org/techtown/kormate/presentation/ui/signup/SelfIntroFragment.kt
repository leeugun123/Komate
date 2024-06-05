package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentSelfIntroBinding
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.domain.model.UserKakaoIntel.userNickName
import org.techtown.kormate.domain.model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.presentation.util.base.BaseFragment
import org.techtown.kormate.presentation.util.extension.showToast

class SelfIntroFragment : BaseFragment<FragmentSelfIntroBinding>(R.layout.fragment_self_intro) {

    private val signUpViewModel: SignUpViewModel by viewModels({ requireParentFragment() })
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        bindingProfileImg()
        binding.userKakaoIntel = UserKakaoIntel
        binding.signUpViewModel = signUpViewModel
        binding.onSelectSelfIntroBtnClick = ::moveToGenderFragment
    }

    private fun bindingProfileImg() {
        Glide.with(requireContext())
            .load(userProfileImg)
            .circleCrop()
            .into(binding.userpic)
    }

    private fun moveToGenderFragment() {
        findNavController().navigate(R.id.action_SelfIntroFragment_to_GenderFragment)
    }
}