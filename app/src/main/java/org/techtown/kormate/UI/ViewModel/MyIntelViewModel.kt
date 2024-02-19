package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Repository.MyIntelRepository

class MyIntelViewModel(application: Application) : AndroidViewModel(application){

    val userIntel : LiveData<UserIntel>

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    private var myIntelRepository : MyIntelRepository

    init {
        myIntelRepository = MyIntelRepository(application)
        userIntel = myIntelRepository.repoFetchUserIntel()
    }

    fun uploadUserIntel(userIntel: UserIntel){

        viewModelScope.launch (Dispatchers.Main){
            _postLiveData.value = myIntelRepository.repoUploadUserIntel(userIntel)
        }

    }


}