package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Repository.MyIntelRepository

class MyIntelViewModel(application: Application) : AndroidViewModel(application){

    private val _userIntel = MutableLiveData<UserIntel>()
    val userIntel: LiveData<UserIntel>
        get() = _userIntel

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    private var myIntelRepository : MyIntelRepository

    init {
        myIntelRepository = MyIntelRepository(application)
    }

    fun fetchUserIntel() {
        myIntelRepository.repoFetchUserIntel()
        _userIntel.value = myIntelRepository.repoGetUserIntel()
    }

    fun uploadUserIntel(userIntel: UserIntel){
        myIntelRepository.repoUploadUserIntel(userIntel)
        _postLiveData.value = myIntelRepository.repoGetUserIntelUploadSuccess()
    }


}