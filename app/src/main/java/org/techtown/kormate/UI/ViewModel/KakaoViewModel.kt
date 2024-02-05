package org.techtown.kormate.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient

class KakaoViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _userProfileImageUrl = MutableLiveData<String>()
    val userProfileImageUrl: LiveData<String>
        get() = _userProfileImageUrl

    private val _userId  = MutableLiveData<Long>()
    val userId : LiveData<Long>
        get() = _userId

    fun loadUserData() {

        UserApiClient.instance.me { user, error ->

            user?.let {
                val nickname = it.kakaoAccount?.profile?.nickname
                _userName.value = nickname!!

                val profileImageUrl = it.kakaoAccount?.profile?.profileImageUrl
                _userProfileImageUrl.value = profileImageUrl!!

                val id = it.id
                _userId.value = id!!

            }

            if(error != null){

                _userName.value = MASTER_ACCOUNT_NAME
                _userProfileImageUrl.value = MASTER_ACCOUNT_PROFILE_IMG_URI
                _userId.value = MASTER_ACCOUNT_USER_ID

            }

        }

    }

    companion object{
        private const val MASTER_ACCOUNT_NAME = "마스터 계정"
        private const val MASTER_ACCOUNT_PROFILE_IMG_URI = "https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"
        private const val MASTER_ACCOUNT_USER_ID : Long = 3333
    }

}