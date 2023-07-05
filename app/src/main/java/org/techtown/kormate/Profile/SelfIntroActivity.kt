package org.techtown.kormate.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.UserIntel
import org.techtown.kormate.Fragment.ViewModel.KakaoViewModel
import org.techtown.kormate.MainActivity
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivitySelfIntroBinding

class SelfIntroActivity : AppCompatActivity() {

    private var binding : ActivitySelfIntroBinding? = null

    private lateinit var kakaoViewModel : KakaoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfIntroBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        kakaoViewModel = ViewModelProvider(this).get(KakaoViewModel::class.java)
        kakaoViewModel.loadUserData()

        kakaoViewModel.userName.observe(this){ userName ->
            binding!!.userName.text = userName.toString()
        }

        kakaoViewModel.userProfileImageUrl.observe(this){ userImg ->
            Glide.with(binding!!.userpic).load(userImg.toString()).circleCrop().into(binding!!.userpic)
        }



        var receivedIntel  = intent.getParcelableExtra<UserIntel>("userIntel")


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


                val intent = Intent(this, GenderActivity::class.java)

                receivedIntel!!.selfIntro = binding!!.selfEdittext.text.toString()

                intent.putExtra("userIntel",receivedIntel)

                startActivity(intent)
                finish()

            }
            else{
                Toast.makeText(this,"자기소개를 작성해주세요.", Toast.LENGTH_SHORT).show()
            }


        }



    }

}