package org.techtown.kormate.UI.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun uploadComment(comment : Comment , postId : String){

        viewModelScope.launch (Dispatchers.IO){

            val responseData = commentRepository.repoUploadComment(comment,postId)

            withContext(Dispatchers.Main){
                _postCommentSuccess.value = responseData
            }

        }

    }


}

