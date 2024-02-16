package org.techtown.kormate.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.BoardDetail


class RecentListRepository(application: Application) {

    private val postRef = Firebase.database.reference.
    child(FirebasePathConstant.POSTS_PATH)

    fun loadRecentLimitData(): LiveData<List<BoardDetail>> {

        val recentLimitListMutableLiveData = MutableLiveData<List<BoardDetail>>()

        postRef.limitToLast(PAGE_LOAD_LIMIT).addValueEventListener(object : ValueEventListener {

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


    fun loadRecentData(): LiveData<List<BoardDetail>> {

        val recentListMutableLiveData = MutableLiveData<List<BoardDetail>>()

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val recentList = mutableListOf<BoardDetail>()

                snapshot.children.reversed().forEach {
                    val boardPost = it.getValue(BoardDetail::class.java)
                    recentList.add(boardPost!!)
                }

                recentListMutableLiveData.value = recentList
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return recentListMutableLiveData

    }



    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }

}