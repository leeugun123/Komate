package org.techtown.kormate.presentation.ViewModel


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

    private val _commentList = MutableLiveData<List<Comment>>()

    val commentList : LiveData<List<Comment>>
        get() = _commentList

    private val _postCommentSuccess = MutableLiveData<Boolean>()

    val postCommentSuccess : LiveData<Boolean>
        get() = _postCommentSuccess

    private val _reportCommentSuccess = MutableLiveData<Boolean>()

    val reportCommentSuccess : LiveData<Boolean>
        get() = _reportCommentSuccess


    private val _deleteCommentSuccess = MutableLiveData<Boolean>()

    val deleteCommentSuccess : LiveData<Boolean>
        get() = _deleteCommentSuccess


    private val commentRepository = CommentRepository()


    fun getComment(){

        viewModelScope.launch(Dispatchers.IO) {

            val responseCommentListData = commentRepository.loadComments()

            withContext(Dispatchers.Main){
                _commentList.value = responseCommentListData
            }

        }

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

            val responseReportSuccess = commentRepository.reportComment(commentReport)

            withContext(Dispatchers.Main){
                _reportCommentSuccess.value = responseReportSuccess
            }

        }

    }

    fun deleteComment(commentId : String){

        viewModelScope.launch(Dispatchers.IO) {

            val responseDeleteCommentSuccess = commentRepository.deleteComment(commentId)

            withContext(Dispatchers.Main){
                _deleteCommentSuccess.value = responseDeleteCommentSuccess
            }

        }


    }


}

