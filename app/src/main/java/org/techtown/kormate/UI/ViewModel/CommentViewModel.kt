package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Repository.CommentRepository

class CommentViewModel(application: Application) : AndroidViewModel(application) {

    private val _commentLiveData = MutableLiveData<List<Comment>>()

    val commentLiveData: LiveData<List<Comment>>
        get() = _commentLiveData

    private var commentRepository : CommentRepository

    init {
        commentRepository = CommentRepository(application)
    }

    suspend fun getComment(postId : String){
        commentRepository.requestComments(postId)
        _commentLiveData.value = commentRepository.getComments()
    }


}

