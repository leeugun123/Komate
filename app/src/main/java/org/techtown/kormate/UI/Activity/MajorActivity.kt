package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityMajorBinding


class MajorActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMajorBinding.inflate(layoutInflater) }
    private val receivedIntel by lazy {intent.getParcelableExtra<UserIntel>("userIntel")}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editMajor.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.checkButton.setBackgroundResource(R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
                val userInput = s?.toString()?.trim() // 사용자의 입력값

                if ((userInput?.length ?: 0) >= 1)
                    binding.checkButton.setBackgroundResource(R.color.blue) // 입력한 글자가 1글자 이상인 경우 버튼의 색상 변경
                else
                    binding.checkButton.setBackgroundResource(R.color.gray) // 입력한 글자가 없는 경우 버튼의 색상 초기화

            }

        })


        binding.checkButton.setOnClickListener {
            checkMajorText()
        }



    }

    private fun checkMajorText() {

        if(binding.editMajor.text.toString().isNotEmpty())
            moveToSelfIntroActivity()
        else
            Toast.makeText(this,"전공을 입력해주세요.",Toast.LENGTH_SHORT).show()
    }

    private fun moveToSelfIntroActivity() {
        val intent = Intent(this, SelfIntroActivity::class.java)
        receivedIntel!!.major = binding.editMajor.text.toString()
        intent.putExtra("userIntel",receivedIntel)
        startActivity(intent)
        finish()
    }


}