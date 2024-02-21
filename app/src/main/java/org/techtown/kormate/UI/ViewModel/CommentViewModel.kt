package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Repository.CommentRepository

class CommentViewModel(application: Application) : AndroidViewModel(application) {

    var commentList : LiveData<List<Comment>>

    private val _postCommentSuccess = MutableLiveData<Boolean>()

    val postCommentSuccess : LiveData<Boolean>
        get() = _postCommentSuccess

    private val _reportCommentSuccess = MutableLiveData<Boolean>()

    val reportCommentSuccess : LiveData<Boolean>
        get() = _reportCommentSuccess


    private var commentRepository : CommentRepository

    init {
        commentRepository = CommentRepository(application)
        commentList = commentRepository.loadComments()
    }

    fun uploadComment(comment : Comment , postId : String){

        viewModelScope.launch (Dispatchers.IO){

            val responseData = commentRepository.uploadComment(comment,postId)

            withContext(Dispatchers.Main){
                _postCommentSuccess.value = responseData
            }

        }

    }

    fun reportComment(commentReport : Report){

        viewModelScope.launch(Dispatchers.IO) {

            val responseData = commentRepository.reportComment(commentReport)

            withContext(Dispatchers.Main){
                _reportCommentSuccess.value = responseData
            }

        }


    }


}

