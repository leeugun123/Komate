package org.techtown.kormate.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.MainActivity
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivitySelfIntroBinding

class SelfIntroActivity : AppCompatActivity() {

    private var binding : ActivitySelfIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfIntroBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        UserApiClient.instance.me { user, _ ->

            "${user?.kakaoAccount?.profile?.nickname}".also {

                if(it != null)
                    binding!!.userName.text = it

            }

            if(user?.kakaoAccount?.profile?.profileImageUrl != null)
                Glide.with(binding!!.userpic).load(user?.kakaoAccount?.profile?.profileImageUrl).circleCrop().into(binding!!.userpic)


        }//내 프로필 사진 카카오 oAuth로 가져오기




        binding!!.selfEdittext.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 입력 전에 호출되는 메서드
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding!!.checkButton.setBackgroundResource(R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {

                val userInput = s?.toString()?.trim() // 사용자의 입력값

                if (userInput?.length ?: 0 >= 1) {
                    // 입력한 글자가 1글자 이상인 경우 버튼의 색상 변경
                    binding!!.checkButton.setBackgroundResource(R.color.blue)
                } else {
                    // 입력한 글자가 없는 경우 버튼의 색상 초기화
                    binding!!.checkButton.setBackgroundResource(R.color.gray)
                }

            }


        })


        binding!!.checkButton.setOnClickListener {

            if(binding!!.selfEdittext.text.toString().isNotEmpty()){

                //서버에서 입력 처리
                val intent = Intent(this, GenderActivity::class.java)
                startActivity(intent)
                finish()

            }
            else{
                Toast.makeText(this,"자기소개를 작성해주세요.", Toast.LENGTH_SHORT).show()
            }


        }



    }

}