package org.techtown.kormate.UI.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Repository.RecentListRepository

class RecentListViewModel(application: Application) : AndroidViewModel(application){

    private var recentLimitList : LiveData<List<BoardDetail>>
    private var recentList : LiveData<List<BoardDetail>>

    private var recentListRepository : RecentListRepository

    init {
        recentListRepository = RecentListRepository(application)
        recentList = recentListRepository.loadRecentData()
        recentLimitList = recentListRepository.loadRecentLimitData()
    }

    fun getRecentLimitData() = recentLimitList
    fun getRecentData() = recentList


}