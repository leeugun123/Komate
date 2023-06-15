package org.techtown.kormate.Fragment.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient

class KakaoViewModel : ViewModel(){

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _userProfileImageUrl = MutableLiveData<String>()
    val userProfileImageUrl: LiveData<String>
        get() = _userProfileImageUrl

    fun loadUserData() {

        UserApiClient.instance.me { user, error ->

            user?.let {
                val nickname = it.kakaoAccount?.profile?.nickname
                _userName.value = nickname + " 님"

                val profileImageUrl = it.kakaoAccount?.profile?.profileImageUrl
                _userProfileImageUrl.value = profileImageUrl!!
            }

        }

    }
}