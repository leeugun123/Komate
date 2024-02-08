package org.techtown.kormate.UI.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Model.UserKakaoIntel.userId
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.UI.ViewModel.MyIntelModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class ReviseActivity : AppCompatActivity() {

    private val binding by lazy { ActivityReviseBinding.inflate(layoutInflater) }
    private val myIntelModel by lazy { ViewModelProvider(this)[MyIntelModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.selfEdittext.setText(UserIntel.selfIntro)
        binding.majorIntel.setText(UserIntel.major)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.reviseButton.setOnClickListener {

            UserIntel.selfIntro = binding.selfEdittext.text.toString()
            UserIntel.major = binding.majorIntel.text.toString()
            writeIntelFirebase()
        }


        myIntelModel.postLiveData.observe(this){ success ->

            if(success){
                Toast.makeText(this,"수정 되었습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }

        }



    }

    private fun writeIntelFirebase() {

        val reference = FirebaseDatabase.getInstance()
                        .reference.child("usersIntel")
                        .child(userId.toString())

        myIntelModel.uploadUserIntel(reference , UserIntel)

    }



}