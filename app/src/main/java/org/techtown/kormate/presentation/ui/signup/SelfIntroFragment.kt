package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentSelfIntroBinding
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.domain.model.UserKakaoIntel.userNickName
import org.techtown.kormate.domain.model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.util.extension.showToast

class SelfIntroFragment : BaseFragment<FragmentSelfIntroBinding>(R.layout.fragment_self_intro) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBinding()

        binding.selfEdittext.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.checkButton.setBackgroundResource(R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
                val userInput = s?.toString()?.trim()

                if ((userInput?.length ?: 0) >= 1)
                    binding.checkButton.setBackgroundResource(R.color.blue) // 입력한 글자가 1글자 이상인 경우 버튼의 색상 변경
                else
                    binding.checkButton.setBackgroundResource(R.color.gray) // 입력한 글자가 없는 경우 버튼의 색상 초기화
            }
        })

        binding.checkButton.setOnClickListener {
            checkSelfEditText()
        }
    }

    private fun uiBinding() {
        profileImgBinding()
        userNameTextBinding()
    }

    private fun userNameTextBinding() {
        binding.userName.text = userNickName
    }

    private fun profileImgBinding() {
        Glide.with(requireContext())
            .load(userProfileImg)
            .circleCrop()
            .into(binding.userpic)
    }

    private fun checkSelfEditText() {
        if (binding.selfEdittext.text.toString().isNotEmpty()) {
            selfIntroBinding()
            moveToGenderFragment()
        } else
            requireContext().showToast("자기소개를 작성해주세요.")
    }

    private fun selfIntroBinding() {
        UserIntel.selfIntro = binding.selfEdittext.text.toString()
    }

    private fun moveToGenderFragment() {
        //TODO("GenderFragment로 이동하는 로직 구현")
    }
}