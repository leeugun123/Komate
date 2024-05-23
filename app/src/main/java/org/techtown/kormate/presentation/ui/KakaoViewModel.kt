package org.techtown.kormate.presentation.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Repository.KakaoRepository

class KakaoViewModel() : ViewModel() {

    private var _kakaoIntelDownloadSuccess = MutableLiveData<Boolean>()
    val kakaoIntelDownloadSuccess: LiveData<Boolean>
        get() = _kakaoIntelDownloadSuccess

    private var _kakaoLogOutSuccess = MutableLiveData<Boolean>()

    val kakaoLogOutSuccess: LiveData<Boolean>
        get() = _kakaoLogOutSuccess

    private val kakaoRepository = KakaoRepository()

    fun loadUserData() {

        viewModelScope.launch(Dispatchers.IO) {

            val responseUploadUserSuccess = kakaoRepository.repoLoadUserData()

            withContext(Dispatchers.Main) {
                _kakaoIntelDownloadSuccess.value = responseUploadUserSuccess
            }

        }

    }

    fun kakaoLogout() {

        viewModelScope.launch(Dispatchers.IO) {

            val responseLogoutSuccess = kakaoRepository.repoKakaoLogout()

            withContext(Dispatchers.Main) {
                _kakaoLogOutSuccess.value = responseLogoutSuccess
            }

        }

    }


}