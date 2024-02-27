package org.techtown.kormate.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.FirebasePathConstant.COMMENT_REPORT_PATH
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Util.BoardData

class CommentRepository() {

    private val ref = Firebase.database
        .reference.child(FirebasePathConstant.POSTS_PATH)

    private val commentReportRef = Firebase.database.reference.child(COMMENT_REPORT_PATH)
    private val commentListMutableLiveData = MutableLiveData<List<Comment>>()


    fun loadComments() : LiveData<List<Comment>> {

        val commentsRef = ref.child(BoardData.boardPostId).child(FirebasePathConstant.COMMENT_PATH)

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


    suspend fun uploadComment(comment : Comment, postId : String) : Boolean {

        val commentRef = ref.child(postId).child(FirebasePathConstant.COMMENT_PATH)
        val commentId = commentRef.push().key.toString()
        comment.id = commentId

        val job = commentRef.child(commentId).setValue(comment)
        job.await()

        return job.isSuccessful

    }

    suspend fun reportComment(commentReport : Report) : Boolean{

        val pushKey = commentReportRef.push().key.toString()
        val job = commentReportRef.child(pushKey).setValue(commentReport)
        job.await()

        return job.isSuccessful

    }




}