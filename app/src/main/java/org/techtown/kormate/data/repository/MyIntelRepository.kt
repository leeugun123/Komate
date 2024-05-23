package org.techtown.kormate.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.techtown.kormate.presentation.constant.FirebasePathConstant.USER_INTEL_PATH
import org.techtown.kormate.domain.model.UserIntel
import org.techtown.kormate.domain.model.UserKakaoIntel.userId

class MyIntelRepository() {

    private val myIntelRef = FirebaseDatabase.getInstance()
        .reference.child(USER_INTEL_PATH).child(userId)


    //const val USER_INTEL_PATH = "usersIntel"


    suspend fun repoFetchUserIntel(): UserIntel {
        return if (checkDataExistence()) {
            getUserIntel() ?: UserIntel
        } else { UserIntel }
    }

    private suspend fun getUserIntel() = myIntelRef.get().await().getValue(UserIntel::class.java)

    suspend fun repoUploadUserIntel(userIntel: UserIntel) : Boolean {

        val job = myIntelRef.setValue(userIntel)
        job.await()
        return job.isSuccessful

    }

    suspend fun checkDataExistence() =
        try { myIntelRef.get().await().exists() }
        catch (e: Exception) { false }


}