package org.techtown.kormate.UI.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Repository.RecentListRepository

class RecentListViewModel(application: Application) : AndroidViewModel(application){

    private val _recentLimitList = MutableLiveData<List<BoardDetail>>()
    val recentLimitList: LiveData<List<BoardDetail>>
        get() = _recentLimitList

    private val _recentList = MutableLiveData<List<BoardDetail>>()

    val recentList : LiveData<List<BoardDetail>>
        get() = _recentList


    private var recentListRepository : RecentListRepository

    init {
        recentListRepository = RecentListRepository(application)
    }

    suspend fun loadRecentLimitData(){
        _recentLimitList.value = recentListRepository.loadRecentLimitData()
    }

    suspend fun loadRecentData(){
        _recentList.value = recentListRepository.loadRecentData()
    }


}