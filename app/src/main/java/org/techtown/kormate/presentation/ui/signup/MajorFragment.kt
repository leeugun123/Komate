package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentMajorBinding
import org.techtown.kormate.presentation.util.base.BaseFragment


class MajorFragment : BaseFragment<FragmentMajorBinding>(R.layout.fragment_major) {

    private val signUpViewModel: SignUpViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding.signUpViewModel = signUpViewModel
        binding.onSelectMajorButtonClick = ::moveToSelfIntroFragment
    }

    private fun moveToSelfIntroFragment() {
        findNavController().navigate(R.id.action_MajorFragment_to_SelfIntroFragment)
    }
}