package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kakao.sdk.user.model.User
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityNationBinding


class NationActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNationBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.radioGroup.setOnCheckedChangeListener { _, checkId ->
            radioCheck(checkId)
        }

        binding.checkButton.setOnClickListener {
            nationCheck()
        }


    }

    private fun nationCheck() {
        if(UserIntel.nation.isBlank())
            Toast.makeText(this,CHECK_NATION_GUIDE,Toast.LENGTH_SHORT).show()
        else
            moveToMajorActivity()
    }

    private fun moveToMajorActivity() {
        startActivity(Intent(this, MajorActivity::class.java))
        finish()
    }

    private fun radioCheck(checkId : Int) {

        when(checkId) {

            binding.radio1.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "한국"
            }

            binding.radio2.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "중국"
            }

            binding.radio3.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "베트남"
            }

            binding.radio4.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "몽골"
            }

            binding.radio5.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                UserIntel.nation = "우즈베키스탄"
            }

        }

    }

    companion object{
        private const val CHECK_NATION_GUIDE = "국가를 체크 해주세요"
    }

}