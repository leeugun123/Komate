package org.techtown.kormate.Repository

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Model.BoardDetail
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BoardPostRepository(application: Application) {

    suspend fun repoUploadPost(boardDetail: BoardDetail) = withContext(Dispatchers.IO) {

        suspendCoroutine { continuation ->

            Firebase.database.reference
                .child(POSTS_PATH).child(boardDetail.postId)
                .setValue(boardDetail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }

        }

    }



}