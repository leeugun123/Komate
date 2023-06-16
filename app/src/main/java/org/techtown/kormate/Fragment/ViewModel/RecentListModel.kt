package org.techtown.kormate.Fragment.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Fragment.Data.BoardDetail

class RecentListModel : ViewModel(){

    private val _recentList = MutableLiveData<List<BoardDetail>>()
    val recentList: LiveData<List<BoardDetail>>
        get() = _recentList

    fun loadRecentData(limit : Boolean) {

        val postRef = Firebase.database.reference.child("posts")

        if(limit){

            postRef.limitToLast(4).addValueEventListener(object : ValueEventListener {
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
        else{

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




    }


}