package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentNationBinding
import org.techtown.kormate.presentation.util.base.BaseFragment


class NationFragment : BaseFragment<FragmentNationBinding>(R.layout.fragment_nation) {

    private val signUpViewModel: SignUpViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
    }

    private fun initBinding() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkId ->
            signUpViewModel.selectNation = checkNation(checkId)
        }
        binding.signUpViewModel = signUpViewModel
        binding.onSelectNationButtonClick = ::moveToMajorFragment
    }

    private fun checkNation(checkId: Int): String {
        return when (checkId) {
            binding.koreaRadioBtn.id -> requireContext().getString(R.string.korea_korean)
            binding.chinaRadioBtn.id -> requireContext().getString(R.string.china_korean)
            binding.vietnamRadioBtn.id -> requireContext().getString(R.string.vietnam_korean)
            binding.mongoliaRadioBtn.id -> requireContext().getString(R.string.mongolia_korean)
            binding.uzbekistanRadioBtn.id -> requireContext().getString(R.string.uzbekistan_korean)
            else -> ""
        }
    }

    private fun moveToMajorFragment() {
        findNavController().navigate(R.id.action_NationFragment_to_MajorFragment)
    }
}