package org.techtown.kormate.Fragment.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Fragment.Data.BoardDetail


class BoardPostViewModel : ViewModel() {

    private val _postLiveData = MutableLiveData<Boolean>()

    val postLiveData: LiveData<Boolean>
        get() = _postLiveData

    fun uploadPost(postsRef : DatabaseReference ,boardDetail: BoardDetail) {

        postsRef.child(boardDetail.postId!!).setValue(boardDetail)
        _postLiveData.value = true

    }



}