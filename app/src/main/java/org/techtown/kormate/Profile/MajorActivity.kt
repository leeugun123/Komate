package org.techtown.kormate.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import org.techtown.kormate.MainActivity
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityMajorBinding


class MajorActivity : AppCompatActivity() {

    private var binding : ActivityMajorBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMajorBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        binding!!.editMajor.addTextChangedListener(object : TextWatcher {

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

            if(binding!!.editMajor.text.toString().isNotEmpty()){

                //서버에서 입력 처리
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"전공을 입력해주세요.",Toast.LENGTH_SHORT).show()
            }


        }



    }


}