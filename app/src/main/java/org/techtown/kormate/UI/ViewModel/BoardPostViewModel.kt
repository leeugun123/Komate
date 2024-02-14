package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Repository.BoardPostRepository


class BoardPostViewModel(application: Application) : AndroidViewModel(application) {

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    private var boardPostRepository : BoardPostRepository

    init {
         boardPostRepository = BoardPostRepository(application)
    }

    suspend fun uploadPost(boardDetail : BoardDetail) {
        _postLiveData.value = boardPostRepository.repoUploadPost(boardDetail)
    }

}