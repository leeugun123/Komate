package org.techtown.kormate.UI.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.scaleMatrix
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.UI.Activity.LoginActivity
import org.techtown.kormate.UI.Activity.MyIntelReviseActivity
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.FragmentMyBinding


class MyFragment : Fragment() {

    private lateinit var binding : FragmentMyBinding
    private val myIntelViewModel : MyIntelViewModel by viewModels()
    private val kakaoViewModel : KakaoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View{
        binding = FragmentMyBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            logoutButton.setOnClickListener {
                showAlertDialog()
            }

            reviseButt.setOnClickListener {
                startActivity(Intent(requireContext(), MyIntelReviseActivity::class.java))
            }

        }

        kakaoIntelBinding()
        observeUserIntelViewModel()

    }

    private fun kakaoIntelBinding() {
        userImgProfileBinding()
        userNameBinding()
    }

    private fun userNameBinding() {
        binding.userName.text = UserKakaoIntel.userNickName
    }

    private fun userImgProfileBinding() {
        Glide.with(this)
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userProfile)
    }

    private fun showAlertDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(LOGOUT_ASK_MESSAGE)

        builder.setPositiveButton(YES_MESSAGE) { dialog, _ ->
            checkKakaoLogOut(dialog)
        }

        builder.setNegativeButton(NO_MESSAGE) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkKakaoLogOut(dialog : DialogInterface) {

        kakaoLogoutRequest()
        kakaoLogOutSuccessObserve(dialog)
    }

    private fun kakaoLogoutRequest() {
        kakaoViewModel.kakaoLogout()
    }

    private fun kakaoLogOutSuccessObserve(dialog : DialogInterface) {

        kakaoViewModel.kakaoLogOutSuccess.observe(viewLifecycleOwner){success ->

            if(success)
                moveToLoginActivity(dialog)
            else
                showToastMessage(LOGOUT_FAIL_MESSAGE)
        }

    }

    private fun showToastMessage(message : String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToLoginActivity(dialog: DialogInterface) {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        Toast.makeText(requireContext(), LOGOUT_SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
        requireActivity().finish()
    }


    @SuppressLint("SetTextI18n")
    private fun observeUserIntelViewModel(){

        myIntelViewModel.userIntel.observe(viewLifecycleOwner){ userIntel ->
            syncUserIntel(userIntel)
            syncUserIntelUiBinding()
        }

    }

    private fun syncUserIntelUiBinding() {

        binding.selfMajor.text = UserIntel.major
        binding.selfIntroText.text = UserIntel.selfIntro
        binding.majorText.text = UserIntel.major
        binding.nationText.text = UserIntel.nation
        binding.genderText.text = UserIntel.gender

        nationTranslationBinding()
    }

    private fun nationTranslationBinding() {

        val nationTranslation = when (UserIntel.nation) {
            "한국" -> "Korea"
            "중국" -> "China"
            "베트남" -> "Vietnam"
            "몽골" -> "Mongolia"
            else -> "Uzbekistan"
        }

        binding.nation.text = nationTranslation
    }

    private fun syncUserIntel(userIntel: UserIntel?) {
        UserIntel.nation = userIntel?.nation.toString()
        UserIntel.major = userIntel?.major.toString()
        UserIntel.gender = userIntel?.gender.toString()
        UserIntel.selfIntro = userIntel?.selfIntro.toString()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_REVISE && resultCode == Activity.RESULT_OK)
            observeUserIntelViewModel()
    }

    companion object {
        const val REQUEST_REVISE = 1
        private const val LOGOUT_ASK_MESSAGE = "로그 아웃 하시겠습니까?"
        private const val LOGOUT_SUCCESS_MESSAGE = "로그아웃 되었습니다."
        private const val LOGOUT_FAIL_MESSAGE = "로그아웃 실패"
        private const val YES_MESSAGE = "예"
        private const val NO_MESSAGE = "아니요"
    }

}