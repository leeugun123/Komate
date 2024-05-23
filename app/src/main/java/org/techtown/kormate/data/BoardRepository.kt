package org.techtown.kormate.data

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.techtown.kormate.presentation.constant.FirebasePathConstant
import org.techtown.kormate.presentation.constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.domain.BoardDetail
import org.techtown.kormate.domain.model.Report

class BoardRepository() {

    private val ref = Firebase.database.reference
    private val postRef = ref.child(POSTS_PATH)

    suspend fun getRecentBoardDetail(): List<BoardDetail> {

        val recentLimitList = mutableListOf<BoardDetail>()

        val dataSnapshot = postRef.get().await()

        dataSnapshot.children.reversed().forEach {

            val boardPost = it.getValue(BoardDetail::class.java)
            recentLimitList.add(boardPost!!)

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