package org.techtown.kormate.Repository

import android.app.Application
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.Comment

class CommentRepository(application: Application) {

    private var commentList = mutableListOf<Comment>()

    fun getComments() = commentList

    suspend fun requestComments(postId: String) = withContext(Dispatchers.IO) {

        val commentsRef = Firebase.database.reference
            .child(FirebasePathConstant.POSTS_PATH)
            .child(postId).child(FirebasePathConstant.COMMENT_PATH)

        commentsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val comment = it.getValue(Comment::class.java)
                    comment?.let { commentList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }


}