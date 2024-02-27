package org.techtown.kormate.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
    private val recentLimitListMutableLiveData = MutableLiveData<List<BoardDetail>>()

    fun getRecentBoardDetail(): LiveData<List<BoardDetail>> {

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val recentLimitList = mutableListOf<BoardDetail>()

                snapshot.children.reversed().forEach {
                    val boardPost = it.getValue(BoardDetail::class.java)
                    recentLimitList.add(boardPost!!)
                }

                recentLimitListMutableLiveData.value = recentLimitList

            }

            override fun onCancelled(error: DatabaseError) {}

        })


        return recentLimitListMutableLiveData

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