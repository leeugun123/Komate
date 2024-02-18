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
import kotlinx.coroutines.withContext
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Util.CurrentDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CommentRepository(application: Application) {


    private val ref = Firebase.database.reference.child(FirebasePathConstant.POSTS_PATH)

    fun loadComments(postId: String) : LiveData<List<Comment>> {

        val commentListMutableLiveData = MutableLiveData<List<Comment>>()
        val commentsRef = ref.child(postId).child(FirebasePathConstant.COMMENT_PATH)

        commentsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val commentList = mutableListOf<Comment>()

                snapshot.children.forEach {
                    val comment = it.getValue(Comment::class.java)
                    commentList.add(comment!!)
                }

                commentListMutableLiveData.value = commentList

            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return commentListMutableLiveData

    }




    suspend fun repoUploadComment(comment : Comment , postId : String) = withContext(Dispatchers.IO) {

        val commentRef = ref.child(postId).child(FirebasePathConstant.COMMENT_PATH)
        val commentId = commentRef.push().key.toString()
        comment.id = commentId

        suspendCoroutine { continuation ->

            commentRef.child(commentId)
                    .setValue(comment)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            continuation.resume(true)
                        else
                            continuation.resume(false)
                    }
        }


    }




}