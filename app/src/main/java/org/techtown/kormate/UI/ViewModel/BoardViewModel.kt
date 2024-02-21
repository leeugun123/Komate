package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Repository.BoardRepository

class BoardViewModel(application: Application) : AndroidViewModel(application) {

    private val _boardPostSuccess = MutableLiveData<Boolean>()

    val boardPostSuccess: LiveData<Boolean>
        get() = _boardPostSuccess

    private val _boardRemoveSuccess = MutableLiveData<Boolean>()
    val boardRemoveSuccess  : LiveData<Boolean>
        get() = _boardRemoveSuccess

    private val _boardReportSuccess = MutableLiveData<Boolean>()

    val boardReportSuccess: LiveData<Boolean>
        get() = _boardReportSuccess


    private var boardRepository : BoardRepository

    init {
        boardRepository = BoardRepository(application)
    }

    fun uploadPost(boardDetail : BoardDetail) {

        viewModelScope.launch (Dispatchers.IO){

            val responseData = boardRepository.repoUploadPost(boardDetail)

            withContext(Dispatchers.Main){
                _boardPostSuccess.value = responseData
            }

        }

    }

    fun removePost(postId : String){

        viewModelScope.launch (Dispatchers.IO){

            val responseData = boardRepository.repoRemovePost(postId)

            withContext(Dispatchers.Main){
                _boardRemoveSuccess.value = responseData
            }

        }

    }

    fun reportPost(report : Report){

        viewModelScope.launch (Dispatchers.IO){

            val responseData = boardRepository.repoBoardReport(report)

            withContext(Dispatchers.Main){
                _boardReportSuccess.value = responseData
            }

        }

    }

}