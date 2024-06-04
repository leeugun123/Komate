package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentMajorBinding
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.util.extension.showToast


class MajorFragment : BaseFragment<FragmentMajorBinding>(R.layout.fragment_major) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editMajor.addTextChangedListener(object : TextWatcher {
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
            checkMajorText()
        }
    }

    private fun checkMajorText() {
        if (binding.editMajor.text.toString().isNotEmpty())
            moveToSelfIntroFragment()
        else
            requireContext().showToast("전공을 입력해주세요.")
    }

    private fun moveToSelfIntroFragment() {
        //TOOD("SelfIntroFragment로 이동하는 로직 구현")
    }

}