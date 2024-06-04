package org.techtown.kormate.presentation.ui.home.board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentBoardBinding
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.FragmentCallback
import org.techtown.kormate.presentation.ui.home.board.detail.CommunityViewModel
import org.techtown.kormate.presentation.ui.home.preview.PreviewAdapter


class BoardFragment : BaseFragment<FragmentBoardBinding>(R.layout.fragment_board),
    FragmentCallback {

    private val communityViewModel: CommunityViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        getBoardList()
        observeRecentList()
    }

    private fun initBinding() {
        binding.onRefreshClick = ::getBoardList
        binding.onPostClick = ::moveToPostFragment
    }

    private fun getBoardList() {
        communityViewModel.getBoardList()
    }
    private fun moveToPostFragment(){
        // TODO("BoardFragment로 이동하는 로직 구현")
    }

    private fun observeRecentList() {
        communityViewModel.boardDetailList.observe(viewLifecycleOwner) { recentList ->
            binding.boardRecyclerview.adapter = PreviewAdapter(recentList)
            binding.boardSwipeRefresh.isRefreshing = false
        }
    }

    override fun onNavigateToBoardFragment(boardDetail: BoardDetail) {
        //TODO("BoardFragment로 이동하는 로직 구현")
    }
}

