package org.techtown.kormate.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.techtown.kormate.MainActivity
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityGenderBinding



class GenderActivity : AppCompatActivity() {

    private var binding : ActivityGenderBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        var gender : String = ""

        binding!!.radioGroup.setOnCheckedChangeListener { group, checkId ->

            when(checkId) {

                binding!!.maleButton.id -> {

                    binding!!.checkButton.setBackgroundResource(R.color.blue)
                    gender = "남성"

                }//남성

                binding!!.FemaleButton.id -> {

                    binding!!.checkButton.setBackgroundResource(R.color.blue)
                    gender = "여성"

                }//여성

            }

        }

        binding!!.checkButton.setOnClickListener {

            if(gender == ""){
                Toast.makeText(this,"성별을 체크해주세요", Toast.LENGTH_SHORT).show()
            }
            else{

                //서버 입력 작업 처리
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }


        }







    }
}