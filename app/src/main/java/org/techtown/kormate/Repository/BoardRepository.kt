package org.techtown.kormate.Repository

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Report
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BoardRepository() {

    private val ref = Firebase.database.reference
    private val postRef = ref.child(POSTS_PATH)

    suspend fun getRecentBoardDetail(): List<BoardDetail> {

        val recentLimitList = mutableListOf<BoardDetail>()

        val dataSnapshot = postRef.get().await()

        if(dataSnapshot.exists()){

            dataSnapshot.children.reversed().forEach{
                recentLimitList.add(it.getValue(BoardDetail::class.java)!!)
            }

        }


        return recentLimitList

    }

    suspend fun repoUploadPost(boardDetail: BoardDetail) : Boolean {

        val job = postRef.child(boardDetail.postId).setValue(boardDetail)
        job.await()
        return job.isSuccessful

    }

    suspend fun repoRemovePost(postId: String) : Boolean {

        val job = postRef.child(postId).removeValue()
        job.await()
        return job.isSuccessful

    }

    suspend fun repoBoardReport(reportContent : Report) : Boolean {

        val reportUid = ref.push().key.toString()
        val job =  ref.child(FirebasePathConstant.POST_REPORT_PATH).child(reportUid).setValue(reportContent)
        job.await()

        return job.isSuccessful

    }





}