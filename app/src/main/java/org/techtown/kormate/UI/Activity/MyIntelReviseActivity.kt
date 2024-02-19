package org.techtown.kormate.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class MyIntelReviseActivity : AppCompatActivity() {

    private val binding by lazy { ActivityReviseBinding.inflate(layoutInflater) }
    private val myIntelViewModel by lazy { ViewModelProvider(this)[MyIntelViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uiSync()


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.reviseButton.setOnClickListener {
            reviseUserIntelBinding()
            reviseUploadUserIntel()
        }

        myIntelViewModel.postLiveData.observe(this){ success ->

            if(success)
                reviseCompleteMessage()

        }


    }

    private fun uiSync() {
        kakaoUserInfoSync()
        binding.selfEdittext.setText(UserIntel.selfIntro)
        binding.majorIntel.setText(UserIntel.major)
    }

    private fun kakaoUserInfoSync() {
        userImgProfileBinding()
        userNameBinding()
    }

    private fun userNameBinding() {
        binding.userName.text = UserKakaoIntel.userNickName
    }

    private fun userImgProfileBinding() {

        Glide.with(this)
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userpic)

    }

    private fun reviseCompleteMessage() {
        Toast.makeText(this, REVISE_COMPLETE_MESSAGE ,Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun reviseUserIntelBinding() {
        UserIntel.selfIntro = binding.selfEdittext.text.toString()
        UserIntel.major = binding.majorIntel.text.toString()
    }

    private fun reviseUploadUserIntel() {
        myIntelViewModel.uploadUserIntel(UserIntel)
    }


    companion object{
        private const val REVISE_COMPLETE_MESSAGE = "수정 되었습니다."
    }



}