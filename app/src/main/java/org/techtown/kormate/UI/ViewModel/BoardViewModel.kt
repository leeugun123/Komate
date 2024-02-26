package org.techtown.kormate.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Repository.BoardRepository

class BoardViewModel() : ViewModel() {


    private val _boardDetailList = MutableLiveData<List<BoardDetail>>()

    val boardDetailList : LiveData<List<BoardDetail>>
        get() = _boardDetailList

    private val _boardPostSuccess = MutableLiveData<Boolean>()

    val boardPostSuccess: LiveData<Boolean>
        get() = _boardPostSuccess

    private val _boardRemoveSuccess = MutableLiveData<Boolean>()
    val boardRemoveSuccess  : LiveData<Boolean>
        get() = _boardRemoveSuccess

    private val _boardReportSuccess = MutableLiveData<Boolean>()

    val boardReportSuccess: LiveData<Boolean>
        get() = _boardReportSuccess

    private val boardRepository = BoardRepository()


    fun getBoardDetailList(){

        viewModelScope.launch(Dispatchers.IO) {

            val responseBoardDetailList = boardRepository.getRecentBoardDetail()

            withContext(Dispatchers.Main){
                _boardDetailList.value = responseBoardDetailList
            }
        }
    }


    fun uploadPost(boardDetail : BoardDetail) {

        viewModelScope.launch(Dispatchers.IO){

            val responseUploadPostSuccess = boardRepository.repoUploadPost(boardDetail)

            withContext(Dispatchers.Main){
                _boardPostSuccess.value = responseUploadPostSuccess
            }

        }

    }

    fun removePost(postId : String){

        viewModelScope.launch (Dispatchers.IO){

            val responseRemoveSuccess = boardRepository.repoRemovePost(postId)

            withContext(Dispatchers.Main){
                _boardRemoveSuccess.value = responseRemoveSuccess
            }


        }

    }

    fun reportPost(report : Report){

        viewModelScope.launch (Dispatchers.IO){

            val responseBoardReportSuccess = boardRepository.repoBoardReport(report)

            withContext(Dispatchers.Main){
                _boardReportSuccess.value = responseBoardReportSuccess
            }

        }

    }

}