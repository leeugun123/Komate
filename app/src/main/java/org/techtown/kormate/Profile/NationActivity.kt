package org.techtown.kormate.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityNationBinding


class NationActivity : AppCompatActivity() {

    private var binding : ActivityNationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityNationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.backBtn.setOnClickListener {
            finish()
        }

        binding!!.radioGroup.setOnCheckedChangeListener{group, checkId ->

            when(checkId) {

                binding!!.radioButton1.id -> {


                }//한국

                binding!!.radioButton2.id -> {


                }//중국

                binding!!.radioButton3.id -> {


                }//베트남

                binding!!.radioButton4.id -> {


                }//몽골

                binding!!.radioButton5.id -> {


                }//우즈벡

            }

        }


    }

}