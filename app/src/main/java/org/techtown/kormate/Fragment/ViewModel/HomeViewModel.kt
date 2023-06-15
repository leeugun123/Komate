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
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.BoardDetail

class HomeViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _userProfileImageUrl = MutableLiveData<String>()
    val userProfileImageUrl: LiveData<String>
        get() = _userProfileImageUrl

    private val _recentList = MutableLiveData<List<BoardDetail>>()
    val recentList: LiveData<List<BoardDetail>>
        get() = _recentList

    fun loadUserData() {

        UserApiClient.instance.me { user, error ->

            user?.let {
                val nickname = it.kakaoAccount?.profile?.nickname
                _userName.value = nickname + " 님"

                val profileImageUrl = it.kakaoAccount?.profile?.profileImageUrl
                _userProfileImageUrl.value = profileImageUrl!!
            }

        }

    }

    fun loadRecentData() {

        val postRef = Firebase.database.reference.child("posts")

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


}
