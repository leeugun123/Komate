package org.techtown.kormate

import org.techtown.kormate.Model.BoardDetail

interface FragmentCallback {
    fun onNavigateToActivity(boardDetail : BoardDetail)
}