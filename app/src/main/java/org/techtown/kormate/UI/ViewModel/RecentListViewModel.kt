package org.techtown.kormate.UI.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Repository.RecentListRepository

class RecentListViewModel() : ViewModel(){

    private val recentListRepository = RecentListRepository()
    val recentLimitList = recentListRepository.loadRecentLimitData()
    val recentList = recentListRepository.loadRecentData()

}