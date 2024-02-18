package org.techtown.kormate.Repository

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Report
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BoardRepository(application: Application) {

    private val ref = Firebase.database.reference
    private val postRef = ref.child(POSTS_PATH)

    suspend fun repoUploadPost(boardDetail: BoardDetail) = withContext(Dispatchers.IO) {

        suspendCoroutine { continuation ->

            postRef.child(boardDetail.postId)
                .setValue(boardDetail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }

        }

    }

    suspend fun repoRemovePost(postId: String) = withContext(Dispatchers.IO) {

        suspendCoroutine { continuation ->

            postRef.child(postId)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }

        }

    }

    suspend fun repoBoardReport(reportContent : Report) = withContext(Dispatchers.IO) {

        suspendCoroutine { continuation ->

            val reportUid = ref.push().key.toString()

            ref.child(FirebasePathConstant.POST_REPORT_PATH)
                .child(reportUid)
                .setValue(reportContent)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }

        }

    }





}