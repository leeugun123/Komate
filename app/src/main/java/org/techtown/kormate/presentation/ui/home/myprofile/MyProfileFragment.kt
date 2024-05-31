package org.techtown.kormate.presentation.ui.home.myprofile

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentMyProfileBinding
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.KakaoViewModel


class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(R.layout.fragment_my_profile) {

    private val myIntelViewModel: MyIntelViewModel by viewModels()
    private val kakaoViewModel: KakaoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        bindUserImg()
        getUserIntel()
        observeViewModel()
    }

    private fun initBinding() {
        binding.onLogoutClick = ::showAlertDialog
        binding.onReviseClick = ::moveToReviseFragment
        binding.userKakoIntel = UserKakaoIntel
        binding.userIntel = UserIntel
    }

    private fun moveToReviseFragment() {
        //TOOD("수정하기 프래그먼트로 이동")
    }

    private fun observeViewModel() {
        observeUserIntelViewModel()
    }

    private fun bindUserImg() {
        Glide.with(requireContext())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userProfile)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.check_logout))
            .setPositiveButton(requireContext().getString(R.string.yes)) { dialog, _ ->
                checkKakaoLogOut(
                    dialog
                )
            }
            .setNegativeButton(requireContext().getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun checkKakaoLogOut(dialog: DialogInterface) {
        kakaoLogoutRequest()
        observeKakaoLogOutSuccess(dialog)
    }

    private fun kakaoLogoutRequest() {
        kakaoViewModel.kakaoLogout()
    }

    private fun observeKakaoLogOutSuccess(dialog: DialogInterface) {
        kakaoViewModel.kakaoLogOutSuccess.observe(viewLifecycleOwner) { success ->
            if (success)
                moveToLoginFragment(dialog)
            else
                showToastMessage(requireContext().getString(R.string.logout_fail))
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToLoginFragment(dialog: DialogInterface) {
        //TODO("로그인 프래그먼트로 이동하는 로직 구현")
    }

    private fun observeUserIntelViewModel() {
        myIntelViewModel.userIntelLiveData.observe(viewLifecycleOwner) { userIntel ->
            binding.userIntel = userIntel
            binding.nation.text = convertNationTranslation(userIntel.nation)
        }
    }

    private fun convertNationTranslation(nation: String) =
        when (nation) {
            requireContext().getString(R.string.korea_korean) -> requireContext().getString(R.string.korea_english)
            requireContext().getString(R.string.china_korean) -> requireContext().getString(R.string.china_english)
            requireContext().getString(R.string.vietnam_korean) -> requireContext().getString(R.string.vietnam_english)
            requireContext().getString(R.string.mongolia_korean) -> requireContext().getString(R.string.mongolia_english)
            else -> requireContext().getString(R.string.uzbekistan_english)
        }

    private fun getUserIntel() {
        myIntelViewModel.getUserIntel()
    }
}