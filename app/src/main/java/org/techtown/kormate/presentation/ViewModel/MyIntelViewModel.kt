package org.techtown.kormate.presentation.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Repository.MyIntelRepository

class MyIntelViewModel() : ViewModel(){


    private val _postSuccessLiveData = MutableLiveData<Boolean>()

    val postSuccessLiveData: LiveData<Boolean>
        get() = _postSuccessLiveData

    private val _dataExistLiveData = MutableLiveData<Boolean>()

    val dataExistLiveData : LiveData<Boolean>
        get() = _dataExistLiveData

    private val _userIntelLiveData = MutableLiveData<UserIntel>()

    val userIntelLiveData  : LiveData<UserIntel>
        get() = _userIntelLiveData

    private val myIntelRepository = MyIntelRepository()

    fun getUserIntel(){

        viewModelScope.launch(Dispatchers.IO){

            val userIntelResponse = myIntelRepository.repoFetchUserIntel()

            withContext(Dispatchers.Main){
                _userIntelLiveData.value = userIntelResponse
            }

        }
    }


    fun uploadUserIntel(userIntel: UserIntel){

        viewModelScope.launch (Dispatchers.IO){

            val responseUploadUserIntelSuccess = myIntelRepository.repoUploadUserIntel(userIntel)

            withContext(Dispatchers.Main){
                _postSuccessLiveData.value = responseUploadUserIntelSuccess
            }

        }

    }

    fun checkDataExist(){

        viewModelScope.launch(Dispatchers.IO) {

            val responseDataExistSuccess = myIntelRepository.checkDataExistence()

            withContext(Dispatchers.Main){
                _dataExistLiveData.value = responseDataExistSuccess
            }

        }

    }


}