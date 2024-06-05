package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.view.View
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentNationBinding
import org.techtown.kormate.presentation.util.base.BaseFragment
import org.techtown.kormate.presentation.util.extension.showToast


class NationFragment : BaseFragment<FragmentNationBinding>(R.layout.fragment_nation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, checkId ->
            radioCheck(checkId)
        }

        binding.checkButton.setOnClickListener {
            nationCheck()
        }
    }

    private fun nationCheck() {
        if(UserIntel.nation.isBlank())
            requireContext().showToast("국가를 체크 해주세요")
        else
            moveToMajorFragment()
    }

    private fun moveToMajorFragment() {
        //TODO("MajorFragment로 이동하는 로직 구현")
    }

    private fun radioCheck(checkId : Int) {

        when(checkId) {

            binding.radio1.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "한국"
            }

            binding.radio2.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "중국"
            }

            binding.radio3.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "베트남"
            }

            binding.radio4.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "몽골"
            }

            binding.radio5.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "우즈베키스탄"
            }
        }
    }
}