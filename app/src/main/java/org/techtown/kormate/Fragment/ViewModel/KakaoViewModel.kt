package org.techtown.kormate.Fragment.ViewModel

import android.util.Log
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

    private var _userId: Long = 0L
    val userId: Long
        get() = _userId

    fun loadUserData() {

        UserApiClient.instance.me { user, error ->

            Log.e("TAG","데이터 가져옴 HomeFragment")

            user?.let {
                val nickname = it.kakaoAccount?.profile?.nickname
                _userName.value = nickname + " 님"

                val profileImageUrl = it.kakaoAccount?.profile?.profileImageUrl
                _userProfileImageUrl.value = profileImageUrl!!

                val id = it.id
                _userId = id ?: 0L
            }

        }

    }
}