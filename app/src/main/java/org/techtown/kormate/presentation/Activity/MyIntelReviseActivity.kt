package org.techtown.kormate.presentation.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.presentation.Fragment.MyFragment
import org.techtown.kormate.presentation.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class MyIntelReviseActivity : AppCompatActivity() {

    private val binding by lazy { ActivityReviseBinding.inflate(layoutInflater) }
    private val myIntelViewModel : MyIntelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uiSync()
        bindingApply()
        viewModelObserve()
    }

    private fun viewModelObserve() {
        postSuccessLiveDataObserve()
    }

    private fun bindingApply() {

        binding.apply {

            backBtn.setOnClickListener {
                finish()
            }

            reviseButton.setOnClickListener {
                reviseUserIntelBinding()
                reviseUploadUserIntel()
            }
        }

    }

    private fun postSuccessLiveDataObserve() {

        myIntelViewModel.postSuccessLiveData.observe(this){ success ->
            if(success)
                reviseCompleteMessage()
        }

    }

    private fun uiSync() {
        kakaoUserInfoSync()

        binding.apply {
            selfEdittext.setText(UserIntel.selfIntro)
            majorIntel.setText(UserIntel.major)
        }

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
        showToastMessage(REVISE_COMPLETE_MESSAGE)
        setResult(MyFragment.RESPONSE_REVISE_CODE)
        finish()
    }

    private fun showToastMessage(message : String){
        Toast.makeText(this, message ,Toast.LENGTH_SHORT).show()
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