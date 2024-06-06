package org.techtown.kormate.presentation.ui.home.board

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentBoardBinding
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.presentation.ui.home.board.detail.CommunityViewModel
import org.techtown.kormate.presentation.ui.home.preview.PreviewAdapter
import org.techtown.kormate.presentation.util.base.BaseFragment


class BoardFragment : BaseFragment<FragmentBoardBinding>(R.layout.fragment_board) {

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

    private fun moveToPostFragment() {
        requireParentFragment().findNavController()
            .navigate(R.id.action_HomeFragment_to_CommunityPostFragment)
    }

    private fun observeRecentList() {
        communityViewModel.boardDetailList.observe(viewLifecycleOwner) { recentList ->
            binding.boardRecyclerview.adapter =
                PreviewAdapter(recentList, ::navigateToCommunityFragment)
            binding.boardSwipeRefresh.isRefreshing = false
        }
    }

    private fun navigateToCommunityFragment(boardDetail: BoardDetail) {
        val bundle = bundleOf("boardDetail" to boardDetail)
        requireParentFragment().findNavController()
            .navigate(R.id.action_HomeFragment_to_CommunityFragment, bundle)
    }

}

