package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.UserIntel
import org.techtown.kormate.databinding.ActivityReviseBinding
import org.techtown.kormate.databinding.FragmentMyBinding


class ReviseActivity : AppCompatActivity() {

    private var binding : ActivityReviseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReviseBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        var receivedIntel  = intent.getParcelableExtra<UserIntel>("userIntel")

        UserApiClient.instance.me { user, _ ->

            "${user?.kakaoAccount?.profile?.nickname}".also {

                if(it != null)
                    binding!!.userName.text = it

            }

            if(user?.kakaoAccount?.profile?.profileImageUrl != null)
                Glide.with(binding!!.userpic).load(user?.kakaoAccount?.profile?.profileImageUrl).circleCrop().into(binding!!.userpic)


        }//내 프로필 사진 카카오 oAuth로 가져오기

        binding!!.selfEdittext.setText(receivedIntel!!.selfIntro.toString())
        //자기소개 가져오기

        binding!!.majorIntel.setText(receivedIntel!!.major.toString())
        //전공 가져오기

        binding!!.backBtn.setOnClickListener {
            finish()
        }

        binding!!.reviseButton.setOnClickListener {

             receivedIntel.selfIntro = binding!!.selfEdittext.text.toString()
             receivedIntel.major = binding!!.majorIntel.text.toString()

            UserApiClient.instance.me { user, error ->

                writeIntelFirebase(receivedIntel, user?.id.toString())

            }//파이베이스에 데이터 올리기


        }



    }

    fun writeIntelFirebase(userIntel: UserIntel , userId: String) {

        // Firebase Realtime Database의 레퍼런스를 가져옵니다.
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("usersIntel").child(userId)

        // UserIntel 객체를 Firebase에 저장합니다.
        reference.setValue(userIntel)

            .addOnSuccessListener {
                Toast.makeText(this,"수정되었습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {

            }


    }


}