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
import org.techtown.kormate.Constant.FirebasePathConstant.COMMENT_PATH
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Model.Comment

class CommentViewModel : ViewModel() {

    private val _commentLiveData = MutableLiveData<List<Comment>>()

    val commentLiveData: LiveData<List<Comment>>
        get() = _commentLiveData

    fun loadComments(postId: String) {

        val commentsRef = Firebase.database.reference.child(POSTS_PATH).child(postId).child(COMMENT_PATH)

        commentsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val commentList = mutableListOf<Comment>()

                for (snapshot in dataSnapshot.children) {

                    val comment = snapshot.getValue(Comment::class.java)

                    comment?.let { commentList.add(it) }

                }
                _commentLiveData.value = commentList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", COMMENT_RECORD_FAIL_MESSAGE)
            }
        })

    }

    companion object{
        private const val COMMENT_RECORD_FAIL_MESSAGE = "댓글 조회 실패"
    }


}

