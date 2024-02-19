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
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.UI.Activity.LoginActivity
import org.techtown.kormate.UI.Activity.ReviseActivity
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.FragmentMyBinding


class MyFragment : Fragment() {

    private lateinit var binding : FragmentMyBinding
    private val myIntelViewModel by lazy { ViewModelProvider(requireActivity())[MyIntelViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View{
        binding = FragmentMyBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutButton.setOnClickListener {
            showAlertDialog()
        }

        binding.reviseButt.setOnClickListener {
            startActivity(Intent(requireContext(), ReviseActivity::class.java))
        }

        observeUserIntelViewModel()

    }


    private fun showAlertDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(LOGOUT_ASK_MESSAGE)

        builder.setPositiveButton("예") { dialog, _ ->
            checkKakaoLogOut(dialog)
        }

        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkKakaoLogOut(dialog : DialogInterface) {

        UserApiClient.instance.logout { error ->
            if (error != null)
                Toast.makeText(requireContext(), LOGOUT_FAIL_MESSAGE, Toast.LENGTH_SHORT).show()
            else
                moveToLoginActivity(dialog)
        }

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
    }

}