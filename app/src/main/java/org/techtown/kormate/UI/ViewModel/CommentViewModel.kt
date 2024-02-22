package org.techtown.kormate.UI.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Repository.CommentRepository

class CommentViewModel() : ViewModel() {

    var commentList : LiveData<List<Comment>>

    private val _postCommentSuccess = MutableLiveData<Boolean>()

    val postCommentSuccess : LiveData<Boolean>
        get() = _postCommentSuccess


    private val commentRepository = CommentRepository()

    init {
        commentList = commentRepository.loadComments()
    }

    fun uploadComment(comment : Comment , postId : String){

        viewModelScope.launch (Dispatchers.IO){

            val responseUploadCommentSuccess = commentRepository.uploadComment(comment,postId)

            withContext(Dispatchers.Main){
                _postCommentSuccess.value = responseUploadCommentSuccess
            }

        }

    }

    fun reportComment(commentReport : Report){

        viewModelScope.launch(Dispatchers.IO) {
            commentRepository.reportComment(commentReport)
        }


    }


}

