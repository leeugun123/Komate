package org.techtown.kormate.UI.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Repository.RecentListRepository

class RecentListViewModel() : ViewModel(){

    private var recentLimitList : LiveData<List<BoardDetail>>
    private var recentList : LiveData<List<BoardDetail>>

    private val recentListRepository = RecentListRepository()

    init {
        recentList = recentListRepository.loadRecentData()
        recentLimitList = recentListRepository.loadRecentLimitData()
    }

    fun getRecentLimitData() = recentLimitList
    fun getRecentData() = recentList


}