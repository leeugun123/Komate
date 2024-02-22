package org.techtown.kormate.UI.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Repository.KakaoRepository

class KakaoViewModel() : ViewModel() {

    private var _KakaoIntelDownloadSuccess = MutableLiveData<Boolean>()
    val KakaoIntelDownloadSuccess : LiveData<Boolean>
        get() = _KakaoIntelDownloadSuccess

    private val kakaoRepository = KakaoRepository()

    fun loadUserData() {

        viewModelScope.launch (Dispatchers.IO){

            val responseUploadUserSuccess = kakaoRepository.repoLoadUserData()

            withContext(Dispatchers.Main){
                _KakaoIntelDownloadSuccess.value = responseUploadUserSuccess
            }

        }


    }


}