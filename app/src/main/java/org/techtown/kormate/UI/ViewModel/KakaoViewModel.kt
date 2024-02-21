package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Repository.KakaoRepository

class KakaoViewModel(application: Application) : AndroidViewModel(application) {

    private var _KakaoIntelDownloadSuccess = MutableLiveData<Boolean>()
    val KakaoIntelDownloadSuccess : LiveData<Boolean>
        get() = _KakaoIntelDownloadSuccess

    private var kakaoRepository : KakaoRepository

    init {
        kakaoRepository = KakaoRepository(application)
    }

    fun loadUserData() {

        viewModelScope.launch (Dispatchers.IO){

            val responseData = kakaoRepository.repoLoadUserData()

            withContext(Dispatchers.Main){
                _KakaoIntelDownloadSuccess.value = responseData
            }

        }


    }


}