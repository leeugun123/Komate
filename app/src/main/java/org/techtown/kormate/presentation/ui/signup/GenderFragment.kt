package org.techtown.kormate.presentation.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentGenderBinding
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.home.MainActivity
import org.techtown.kormate.presentation.ui.home.myprofile.MyIntelViewModel
import org.techtown.kormate.presentation.util.extension.showToast


class GenderFragment : BaseFragment<FragmentGenderBinding>(R.layout.fragment_gender) {

    private val myIntelViewModel: MyIntelViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                binding.maleButton.id -> syncCheckButton("남성")
                binding.FemaleButton.id -> syncCheckButton("여성")
            }
        }

        binding.checkButton.setOnClickListener {
            if (UserIntel.gender.isBlank())
                requireContext().showToast("성별을 체크 해주세요")
            else {
                uploadUserInfo()
                moveHomeFragment()
            }
        }

        myIntelViewModel.postSuccessLiveData.observe(viewLifecycleOwner) {
            requireContext().showToast("정보가 입력 되었습니다.")
        }
    }

    private fun syncCheckButton(gender: String) {
        binding.checkButton.setBackgroundResource(R.color.blue)
        UserIntel.gender = gender
    }

    private fun uploadUserInfo() {
        myIntelViewModel.uploadUserIntel(UserIntel)
    }

    private fun moveHomeFragment() {
        //TODO("HomeFragment로 이동하는 로직 구현")
    }
}