package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        viewModelScope.launch (Dispatchers.Main){
            _boardPostSuccess.value = boardRepository.repoUploadPost(boardDetail)
        }

    }

    fun removePost(postId : String){

        viewModelScope.launch (Dispatchers.Main){
            _boardRemoveSuccess.value = boardRepository.repoRemovePost(postId)
        }
    }

    fun reportPost(report : Report){

        viewModelScope.launch (Dispatchers.Main){
            _boardReportSuccess.value = boardRepository.repoBoardReport(report)
        }

    }

}