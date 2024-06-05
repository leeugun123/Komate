package org.techtown.kormate.presentation.util

import org.techtown.kormate.domain.model.BoardDetail

interface FragmentCallback {
    fun onNavigateToBoardFragment(boardDetail : BoardDetail)
}