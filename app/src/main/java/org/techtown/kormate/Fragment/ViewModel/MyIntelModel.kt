package org.techtown.kormate.Fragment.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.sdk.user.model.User
import org.techtown.kormate.Fragment.Data.UserIntel

class MyIntelModel : ViewModel(){

    private val _userIntel = MutableLiveData<UserIntel>()

    val userIntel: LiveData<UserIntel>
        get() = _userIntel

    fun fetchUserIntel(userId : String) {

        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("usersIntel").child(userId)

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


}