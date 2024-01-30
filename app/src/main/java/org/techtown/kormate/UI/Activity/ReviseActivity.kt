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
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.UI.ViewModel.MyIntelModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class ReviseActivity : AppCompatActivity() {

    private var binding : ActivityReviseBinding? = null

    private var receivedIntel : UserIntel? = null

    private lateinit var kakaoViewModel : KakaoViewModel
    private lateinit var myIntelModel : MyIntelModel

    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReviseBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        kakaoViewModel = ViewModelProvider(this).get(KakaoViewModel::class.java)
        kakaoViewModel.loadUserData()
        observeKakaoModel()

        myIntelModel = ViewModelProvider(this).get(MyIntelModel::class.java)


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


        myIntelModel.postLiveData.observe(this){ success ->

            if(success){

                val intent = Intent()
                intent.putExtra("userIntel",receivedIntel)
                setResult(Activity.RESULT_OK, intent)

                Toast.makeText(this,"수정되었습니다.",Toast.LENGTH_SHORT).show()
                finish()

            }

        }



    }

    private fun writeIntelFirebase(userIntel: UserIntel, userId: String) {

        val reference = FirebaseDatabase.getInstance()
                        .reference.child("usersIntel")
                        .child(userId)

        myIntelModel.uploadUserIntel(reference,userIntel)

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