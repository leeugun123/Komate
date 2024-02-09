package org.techtown.kormate.UI.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Model.BoardDetail

class RecentListModel : ViewModel(){

    private val _recentList = MutableLiveData<List<BoardDetail>>()
    val recentList: LiveData<List<BoardDetail>>
        get() = _recentList

    private val postRef by lazy { Firebase.database.reference.child(POSTS_PATH)}

    fun loadRecentData(limit : Boolean) {

        if(limit)
            pageLimitLoad()
        else
            pageEntireLoad()

    }

    private fun pageEntireLoad() {

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val recentList = mutableListOf<BoardDetail>()

                for (snapshot in snapshot.children.reversed()) {
                    val post = snapshot.getValue(BoardDetail::class.java)
                    post?.let { recentList.add(it) }
                }

                _recentList.value = recentList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", error.toString())
            }

        })
    }

    private fun pageLimitLoad() {

        postRef.limitToLast(PAGE_LOAD_LIMIT).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val recentList = mutableListOf<BoardDetail>()

                for (snapshot in snapshot.children.reversed()) {
                    val post = snapshot.getValue(BoardDetail::class.java)
                    post?.let { recentList.add(it) }
                }

                _recentList.value = recentList
            }

            override fun onCancelled(error: DatabaseError) { Log.e("TAG", error.toString()) }
        })

    }



    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }


}