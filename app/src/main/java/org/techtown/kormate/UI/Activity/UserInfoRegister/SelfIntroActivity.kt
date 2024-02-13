package org.techtown.kormate.UI.Activity.UserInfoRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivitySelfIntroBinding

class SelfIntroActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySelfIntroBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uiBinding()

        binding.selfEdittext.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.checkButton.setBackgroundResource(R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {

                val userInput = s?.toString()?.trim()

                if ((userInput?.length ?: 0) >= 1)
                    binding.checkButton.setBackgroundResource(R.color.blue) // 입력한 글자가 1글자 이상인 경우 버튼의 색상 변경
                else
                    binding.checkButton.setBackgroundResource(R.color.gray) // 입력한 글자가 없는 경우 버튼의 색상 초기화
            }

        })

        binding.checkButton.setOnClickListener {
            checkSelfEditText()
        }

    }

    private fun uiBinding() {
        profileImgBinding()
        userNameTextBinding()
    }

    private fun userNameTextBinding() {
        binding.userName.text = userNickName
    }

    private fun profileImgBinding() {
        Glide.with(this)
            .load(userProfileImg)
            .circleCrop()
            .into(binding.userpic)
    }

    private fun checkSelfEditText() {

        if(binding.selfEdittext.text.toString().isNotEmpty())
            moveToGenderActivity()
        else
            Toast.makeText(this, SELF_INTRO_GUIDE, Toast.LENGTH_SHORT).show()
    }

    private fun moveToGenderActivity() {
        startActivity(Intent(this, GenderActivity::class.java))
        finish()
    }


    companion object{
        private const val SELF_INTRO_GUIDE = "자기소개를 작성해주세요."
    }

}