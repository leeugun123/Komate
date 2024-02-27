package org.techtown.kormate.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class MyIntelReviseActivity : AppCompatActivity() {

    private val binding by lazy { ActivityReviseBinding.inflate(layoutInflater) }
    private val myIntelViewModel : MyIntelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uiSync()

        binding.apply {

            backBtn.setOnClickListener {
                finish()
            }

            reviseButton.setOnClickListener {
                reviseUserIntelBinding()
                reviseUploadUserIntel()
            }

        }

        postSuccessLiveDataObserve()


    }

    private fun postSuccessLiveDataObserve() {

        myIntelViewModel.postSuccessLiveData.observe(this){ success ->
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