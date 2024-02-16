package org.techtown.kormate.Repository

import android.app.Application
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.BoardDetail
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RecentListRepository(application: Application) {

    private val postRef = Firebase.database.reference.
    child(FirebasePathConstant.POSTS_PATH)


    suspend fun loadRecentLimitData(): List<BoardDetail> = suspendCoroutine { continuation ->

        val recentLimitList = mutableListOf<BoardDetail>()

        postRef.limitToLast(PAGE_LOAD_LIMIT).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                recentLimitList.clear()

                snapshot.children.reversed().forEach { it ->
                    val boardPost = it.getValue(BoardDetail::class.java)
                    boardPost?.let { recentLimitList.add(it) }
                }

                continuation.resume(recentLimitList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", error.toString())
                continuation.resumeWithException(error.toException())
            }

        })

    }


    suspend fun loadRecentData(): List<BoardDetail> = suspendCoroutine { continuation ->

        val recentList = mutableListOf<BoardDetail>()

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                recentList.clear()

                snapshot.children.reversed().forEach { it ->
                    val boardPost = it.getValue(BoardDetail::class.java)
                    boardPost?.let { recentList.add(it) }
                }

                continuation.resume(recentList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", error.toString())
                continuation.resumeWithException(error.toException())
            }

        })

    }



    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }

}