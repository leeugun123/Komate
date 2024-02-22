package org.techtown.kormate.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.FirebasePathConstant.USER_INTEL_PATH
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Model.UserKakaoIntel.userId
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyIntelRepository() {

    private val myIntelRef = FirebaseDatabase.getInstance()
        .reference.child(USER_INTEL_PATH).child(userId)

    private val userIntelMutableLiveData = MutableLiveData<UserIntel>()


    fun repoFetchUserIntel() : LiveData<UserIntel> {

        CoroutineScope(Dispatchers.IO).launch{
            if(checkDataExistence())
                getUserId()
        }

        return userIntelMutableLiveData

    }

    private fun getUserId() {

        myIntelRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userIntel = dataSnapshot.getValue(UserIntel::class.java)!!
                userIntelMutableLiveData.value = userIntel
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    suspend fun repoUploadUserIntel(userIntel: UserIntel) : Boolean {

        val job = myIntelRef.setValue(userIntel)
        job.await()
        return job.isSuccessful

    }



    suspend fun checkDataExistence() =
        try {
            myIntelRef.get().await().exists()
        }
        catch (e: Exception) { false }




}