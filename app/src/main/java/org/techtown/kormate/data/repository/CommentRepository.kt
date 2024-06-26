package org.techtown.kormate.data.repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.techtown.kormate.presentation.constant.FirebasePathConstant
import org.techtown.kormate.presentation.constant.FirebasePathConstant.COMMENT_REPORT_PATH
import org.techtown.kormate.domain.model.Comment
import org.techtown.kormate.domain.model.Report
import org.techtown.kormate.presentation.util.BoardData

class CommentRepository() {

    private val postsPathRef = Firebase.database
        .reference.child(FirebasePathConstant.POSTS_PATH)

    private val commentReportRef = Firebase.database.reference.child(COMMENT_REPORT_PATH)

    private val commentList = mutableListOf<Comment>()

    suspend fun loadComments() : List<Comment> {

        val commentsRef = postsPathRef.child(BoardData.boardPostId).child(FirebasePathConstant.COMMENT_PATH)

        val dataSnapshot = commentsRef.get().await()

        commentList.clear()

        dataSnapshot.children.forEach { comment ->
            commentList.add(comment.getValue(Comment::class.java)!!)
        }

        return commentList

    }


    suspend fun uploadComment(comment : Comment, postId : String) : Boolean {

        val commentRef = postsPathRef.child(postId).child(FirebasePathConstant.COMMENT_PATH)
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

    suspend fun deleteComment(commentId : String) : Boolean {

        val commentsRef = postsPathRef.child(BoardData.boardPostId).child(FirebasePathConstant.COMMENT_PATH)

        val job = commentsRef.child(commentId).removeValue()
        job.await()

        return job.isSuccessful

    }




}