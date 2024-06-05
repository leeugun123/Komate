package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentGenderBinding
import org.techtown.kormate.presentation.util.base.BaseFragment


class GenderFragment : BaseFragment<FragmentGenderBinding>(R.layout.fragment_gender) {

    private val signUpViewModel: SignUpViewModel by viewModels({ requireParentFragment() })
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
    }

    private fun initBinding() {
        binding.signUpViewModel = signUpViewModel
        binding.onSelectGenderBtnClick = ::moveToHomeFragment
        binding.radioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                binding.maleButton.id -> signUpViewModel.gender = "남성"
                binding.FemaleButton.id -> signUpViewModel.gender = "여성"
            }
        }
    }

    private fun moveToHomeFragment() {
        signUpViewModel.join()
        requireParentFragment().findNavController().navigate(R.id.action_SignUpFragment_to_HomeFragment)
    }
}