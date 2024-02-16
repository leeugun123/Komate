package org.techtown.kormate.Repository

import android.app.Application
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel

class MyIntelRepository(application: Application) {

    private lateinit var userIntel : UserIntel
    private var userIntelUploadSuccess = false

    fun repoGetUserIntel() = userIntel

    fun repoGetUserIntelUploadSuccess() = userIntelUploadSuccess

    fun repoFetchUserIntel(){

        val reference = FirebaseDatabase.getInstance()
            .reference.child(FirebasePathConstant.USER_INTEL_PATH)
            .child(UserKakaoIntel.userId)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userIntel = dataSnapshot.getValue(UserIntel::class.java)!!
            }

            override fun onCancelled(databaseError: DatabaseError) {}

        })

    }

    fun repoUploadUserIntel(userIntel: UserIntel){

        userIntelUploadSuccess = false

        FirebaseDatabase.getInstance()
            .reference.child(FirebasePathConstant.USER_INTEL_PATH)
            .child(UserKakaoIntel.userId).setValue(userIntel).addOnSuccessListener {
                userIntelUploadSuccess = true
            }

    }




}