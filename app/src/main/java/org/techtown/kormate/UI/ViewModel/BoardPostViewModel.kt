package org.techtown.kormate.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import org.techtown.kormate.Model.BoardDetail


class BoardPostViewModel : ViewModel() {

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    fun uploadPost(postsRef : DatabaseReference ,boardDetail: BoardDetail) {

        postsRef.child(boardDetail.postId).setValue(boardDetail)
        _postLiveData.value = true

    }



}