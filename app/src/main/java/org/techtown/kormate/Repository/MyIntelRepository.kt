package org.techtown.kormate.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.FirebasePathConstant.USER_INTEL_PATH
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Model.UserKakaoIntel.userId
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyIntelRepository(application: Application) {

    private val myIntelRef = FirebaseDatabase.getInstance()
        .reference.child(USER_INTEL_PATH)
        .child(userId)


    fun repoFetchUserIntel() : LiveData<UserIntel> {

        val userIntelMutableLiveData = MutableLiveData<UserIntel>()

        myIntelRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userIntel = dataSnapshot.getValue(UserIntel::class.java)!!
                userIntelMutableLiveData.value = userIntel
            }

            override fun onCancelled(databaseError: DatabaseError) {}

        })

        return userIntelMutableLiveData

    }

    suspend fun repoUploadUserIntel(userIntel: UserIntel) = withContext(Dispatchers.IO){

        suspendCoroutine { continuation ->

            myIntelRef.setValue(userIntel)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful)
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }

        }

    }




}