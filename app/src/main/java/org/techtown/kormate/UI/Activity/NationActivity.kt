package org.techtown.kormate.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityNationBinding


class NationActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNationBinding.inflate(layoutInflater)}
    private lateinit var nation : String
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
        if(nation == "")
            Toast.makeText(this,"국가를 체크해주세요",Toast.LENGTH_SHORT).show()
        else
            moveToMajorActivity()
    }

    private fun moveToMajorActivity() {
        val intent = Intent(this, MajorActivity::class.java)
        intent.putExtra("userIntel", UserIntel(nation,"","",""))
        startActivity(intent)
        finish()
    }

    private fun radioCheck(checkId : Int) {

        when(checkId) {

            binding.radio1.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                nation = "한국"
            }

            binding.radio2.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                nation = "중국"
            }

            binding.radio3.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                nation = "베트남"
            }

            binding.radio4.id -> {
                binding!!.checkButton.setBackgroundResource(R.color.blue)
                nation = "몽골"
            }

            binding.radio5.id -> {
                binding.checkButton.setBackgroundResource(R.color.blue)
                nation = "우즈베키스탄"
            }

        }
    }

}