package org.techtown.kormate.presentation

import org.techtown.kormate.domain.BoardDetail

interface FragmentCallback {
    fun onNavigateToBoardFragment(boardDetail : BoardDetail)
}