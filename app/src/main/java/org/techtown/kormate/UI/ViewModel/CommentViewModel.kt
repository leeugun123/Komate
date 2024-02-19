package org.techtown.kormate.UI.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Repository.CommentRepository
import org.techtown.kormate.UI.Activity.BoardActivity

class CommentViewModel(application: Application) : AndroidViewModel(application) {

   var commentList : LiveData<List<Comment>>


    private val _postCommentSuccess = MutableLiveData<Boolean>()

    val postCommentSuccess : LiveData<Boolean>
        get() = _postCommentSuccess

    private var commentRepository : CommentRepository

    init {
        commentRepository = CommentRepository(application)
        commentList = commentRepository.loadComments()
    }

    suspend fun uploadComment(comment : Comment , postId : String){
        _postCommentSuccess.value = commentRepository.repoUploadComment(comment,postId)
    }


}

