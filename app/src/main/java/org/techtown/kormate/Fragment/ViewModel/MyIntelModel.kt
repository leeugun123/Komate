package org.techtown.kormate.Fragment.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.kakao.sdk.user.model.User
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.UserIntel

class MyIntelModel : ViewModel(){

    private val _userIntel = MutableLiveData<UserIntel>()
    val userIntel: LiveData<UserIntel>
        get() = _userIntel

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    fun fetchUserIntel(userId : Long) {

        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("usersIntel").child(userId.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userIntel = dataSnapshot.getValue(UserIntel::class.java)
                _userIntel.value = userIntel
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

    }

    fun uploadUserIntel(postsRef : DatabaseReference, userIntel: UserIntel){

        postsRef.setValue(userIntel)
        _postLiveData.value = true

    }





}