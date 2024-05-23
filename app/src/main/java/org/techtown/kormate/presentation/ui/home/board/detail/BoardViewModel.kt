package org.techtown.kormate.presentation.ui.home.board.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.domain.BoardDetail
import org.techtown.kormate.domain.model.Report
import org.techtown.kormate.data.BoardRepository

class BoardViewModel() : ViewModel() {

    private val _boardPostSuccess = MutableLiveData<Boolean>()

    val boardPostSuccess: LiveData<Boolean>
        get() = _boardPostSuccess

    private val _boardRemoveSuccess = MutableLiveData<Boolean>()
    val boardRemoveSuccess: LiveData<Boolean>
        get() = _boardRemoveSuccess

    private val _boardReportSuccess = MutableLiveData<Boolean>()

    val boardReportSuccess: LiveData<Boolean>
        get() = _boardReportSuccess

    private val _boardDetailList = MutableLiveData<List<BoardDetail>>()
    val boardDetailList: LiveData<List<BoardDetail>>
        get() = _boardDetailList

    private val boardRepository = BoardRepository()

    fun getBoardList() {

        viewModelScope.launch(Dispatchers.IO) {

            val responseBoardList = boardRepository.getRecentBoardDetail()

            withContext(Dispatchers.Main) {
                _boardDetailList.value = responseBoardList
            }

        }
    }

    fun uploadPost(boardDetail: BoardDetail) {

        viewModelScope.launch(Dispatchers.IO) {

            val responseUploadPostSuccess = boardRepository.repoUploadPost(boardDetail)

            withContext(Dispatchers.Main) {
                _boardPostSuccess.value = responseUploadPostSuccess
            }
        }
    }

    fun removePost(postId: String) {

        viewModelScope.launch(Dispatchers.IO) {

            val responseRemoveSuccess = boardRepository.repoRemovePost(postId)

            withContext(Dispatchers.Main) {
                _boardRemoveSuccess.value = responseRemoveSuccess
            }
        }
    }

    fun reportPost(report: Report) {

        viewModelScope.launch(Dispatchers.IO) {

            val responseBoardReportSuccess = boardRepository.repoBoardReport(report)

            withContext(Dispatchers.Main) {
                _boardReportSuccess.value = responseBoardReportSuccess
            }

        }

    }

}