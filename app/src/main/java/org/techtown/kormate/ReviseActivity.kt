package org.techtown.kormate

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.UserIntel
import org.techtown.kormate.Fragment.ViewModel.KakaoViewModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class ReviseActivity : AppCompatActivity() {

    private var binding : ActivityReviseBinding? = null

    private var receivedIntel : UserIntel? = null

    lateinit var kakaoViewModel : KakaoViewModel

    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReviseBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        kakaoViewModel = ViewModelProvider(this).get(KakaoViewModel::class.java)
        kakaoViewModel.loadUserData()
        observeKakaoModel()


        receivedIntel = intent.getParcelableExtra("userIntel")



        binding!!.selfEdittext.setText(receivedIntel!!.selfIntro.toString())
        //자기소개 가져오기

        binding!!.majorIntel.setText(receivedIntel!!.major.toString())
        //전공 가져오기


        binding!!.backBtn.setOnClickListener {
            finish()
        }

        binding!!.reviseButton.setOnClickListener {

             receivedIntel!!.selfIntro = binding!!.selfEdittext.text.toString()
             receivedIntel!!.major = binding!!.majorIntel.text.toString()

            writeIntelFirebase(receivedIntel!!, userId.toString())

        }



    }

    private fun writeIntelFirebase(userIntel: UserIntel, userId: String) {

        // Firebase Realtime Database의 레퍼런스를 가져옵니다.
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("usersIntel").child(userId)

        // UserIntel 객체를 Firebase에 저장합니다.
        reference.setValue(userIntel)
            .addOnSuccessListener {

                val intent = Intent()
                intent.putExtra("userIntel",receivedIntel)
                setResult(Activity.RESULT_OK, intent)

                Toast.makeText(this,"수정되었습니다.",Toast.LENGTH_SHORT).show()
                finish()


            }
            .addOnFailureListener {

            }


    }

    private fun observeKakaoModel(){

        kakaoViewModel.userName.observe(this) { userName ->
            binding?.userName?.text = userName
        }

        kakaoViewModel.userProfileImageUrl.observe(this) { imageUrl ->
            Glide.with(binding!!.userpic).load(imageUrl).circleCrop().into(binding!!.userpic)
        }

        kakaoViewModel.userId.observe(this){ userId ->
            this.userId = userId
        }


    }


}