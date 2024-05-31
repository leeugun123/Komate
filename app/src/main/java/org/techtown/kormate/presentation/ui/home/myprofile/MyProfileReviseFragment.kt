package org.techtown.kormate.presentation.ui.home.myprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentMyProfileReviseBinding
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.showToast

class MyProfileReviseFragment :
    BaseFragment<FragmentMyProfileReviseBinding>(R.layout.fragment_my_profile_revise) {

    private val myIntelViewModel: MyIntelViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        bindUserImgProfile()
        observePostSuccess()
    }

    private fun initBinding() {
        binding.userKakoIntel = UserKakaoIntel
        binding.userIntel = UserIntel
        binding.onBackClick = ::moveToMyProfileFragment
        binding.onReviseClick = ::reviseUploadUserIntel
    }

    private fun moveToMyProfileFragment(){
        // TODO("myProfile로 돌아가는 로직 구현")
    }

    private fun observePostSuccess() {
        myIntelViewModel.postSuccessLiveData.observe(viewLifecycleOwner) { success ->
            if (success)
                requireContext().showToast(R.string.revise_complete)
        }
    }

    private fun bindUserImgProfile() {
        Glide.with(requireContext())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userpic)
    }

    private fun reviseUploadUserIntel() {
        myIntelViewModel.uploadUserIntel(UserIntel)
    }
}