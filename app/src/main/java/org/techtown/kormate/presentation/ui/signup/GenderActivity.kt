package org.techtown.kormate.presentation.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityGenderBinding
import org.techtown.kormate.presentation.ui.home.MainActivity
import org.techtown.kormate.presentation.ui.home.myprofile.MyIntelViewModel


class GenderActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGenderBinding.inflate(layoutInflater) }
    private val myIntelViewModel by lazy { ViewModelProvider(this)[MyIntelViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.radioGroup.setOnCheckedChangeListener { _, checkId ->

            when (checkId) {
                binding.maleButton.id -> syncCheckButton("남성")
                binding.FemaleButton.id -> syncCheckButton("여성")
            }

        }

        binding.checkButton.setOnClickListener {

            if (UserIntel.gender.isBlank())
                printCheckGenderToastMessage()
            else {
                uploadUserInfo()
                moveMainActivity()
            }

        }

        myIntelViewModel.postSuccessLiveData.observe(this) {
            Toast.makeText(this, INPUT_INFO_COMPLETE_GUIDE, Toast.LENGTH_SHORT).show()
        }


    }

    private fun syncCheckButton(gender: String) {
        binding.checkButton.setBackgroundResource(R.color.blue)
        UserIntel.gender = gender
    }

    private fun uploadUserInfo() {
        myIntelViewModel.uploadUserIntel(UserIntel)
    }

    private fun moveMainActivity() {
        Toast.makeText(this, INPUT_INFO_GUIDE, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun printCheckGenderToastMessage() {
        Toast.makeText(this, CHECK_GENDER_GUIDE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val INPUT_INFO_COMPLETE_GUIDE = "정보가 입력 되었습니다."
        private const val CHECK_GENDER_GUIDE = "성별을 체크 해주세요"
        private const val INPUT_INFO_GUIDE = "정보가 입력 되었습니다."
    }

}