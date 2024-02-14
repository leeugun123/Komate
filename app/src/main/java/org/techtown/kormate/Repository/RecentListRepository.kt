package org.techtown.kormate.Repository

import android.app.Application
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.BoardDetail

class RecentListRepository(application: Application) {


    private var recentList = mutableListOf<BoardDetail>()

    private val postRef by lazy { Firebase.database.reference.child(FirebasePathConstant.POSTS_PATH)}

    fun getRecentData() = recentList

    suspend fun loadRecentData(limit : Boolean) {

        recentListInit()

        if(limit)
            pageLimitLoad()
        else
            pageEntireLoad()

    }

    private fun recentListInit() {
        recentList.clear()
    }

    private fun pageEntireLoad() {

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.reversed().forEach { it ->
                    val post = it.getValue(BoardDetail::class.java)
                    post?.let { recentList.add(it) }
                }

            }

            override fun onCancelled(error: DatabaseError) { Log.e("TAG", error.toString()) }
        })

    }

    private fun pageLimitLoad() {

        postRef.limitToLast(PAGE_LOAD_LIMIT).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.reversed().forEach { it ->
                    val post = it.getValue(BoardDetail::class.java)
                    post?.let { recentList.add(it) }
                }

            }

            override fun onCancelled(error: DatabaseError) { Log.e("TAG", error.toString()) }
        })

    }

    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }

}