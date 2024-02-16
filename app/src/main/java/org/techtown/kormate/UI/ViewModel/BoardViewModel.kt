package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Repository.BoardPostRepository

class BoardViewModel(application: Application) : AndroidViewModel(application) {

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    private val _removeLiveData = MutableLiveData<Boolean>()
    val removeLiveData  : LiveData<Boolean>
        get() = _removeLiveData

    private val _reportLiveData = MutableLiveData<Boolean>()

    val reportLiveData: LiveData<Boolean>
        get() = _reportLiveData


    private var boardPostRepository : BoardPostRepository

    init {
        boardPostRepository = BoardPostRepository(application)
    }

    suspend fun uploadPost(boardDetail : BoardDetail) {
        _postLiveData.value = boardPostRepository.repoUploadPost(boardDetail)
    }

    suspend fun removePost(postId : String){
        _removeLiveData.value = boardPostRepository.repoRemovePost(postId)
    }

    suspend fun reportPost(report : Report){
        _reportLiveData.value = boardPostRepository.repoBoardReport(report)
    }

}