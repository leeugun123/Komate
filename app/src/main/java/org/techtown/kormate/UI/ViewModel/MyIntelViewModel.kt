package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Repository.MyIntelRepository

class MyIntelViewModel(application: Application) : AndroidViewModel(application){

    val userIntel : LiveData<UserIntel>

    private val _postSuccessLiveData = MutableLiveData<Boolean>()

    val postSuccessLiveData: LiveData<Boolean>
        get() = _postSuccessLiveData

    private val _dataExistLiveData = MutableLiveData<Boolean>()

    val dataExistLiveData : LiveData<Boolean>
        get() = _dataExistLiveData

    private var myIntelRepository : MyIntelRepository

    init {
        myIntelRepository = MyIntelRepository(application)
        userIntel = myIntelRepository.repoFetchUserIntel()
    }

    fun uploadUserIntel(userIntel: UserIntel){

        viewModelScope.launch (Dispatchers.Main){
            _postSuccessLiveData.value = myIntelRepository.repoUploadUserIntel(userIntel)
        }

    }

    fun checkDataExist(){

        viewModelScope.launch(Dispatchers.IO) {

            val responseData = myIntelRepository.checkDataExistence()

            withContext(Dispatchers.Main){
                _dataExistLiveData.value = responseData
            }

        }

    }


}