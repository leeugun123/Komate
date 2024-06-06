package org.techtown.kormate.presentation.ui.home.preview

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentPreviewBinding
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.presentation.ui.home.board.detail.CommunityViewModel
import org.techtown.kormate.presentation.util.base.BaseFragment

class PreviewFragment : BaseFragment<FragmentPreviewBinding>(R.layout.fragment_preview) {

    private val communityViewModel: CommunityViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        observeRecentLimitList()
    }

    private fun initBinding() {
        bindingProfileImg()
        getBoardContentList()
        binding.userKakoIntel = UserKakaoIntel
        binding.onRefreshClick = ::getBoardContentList
    }

    private fun bindingProfileImg() {
        Glide.with(requireContext())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userProfile)
    }

    private fun observeRecentLimitList() {
        communityViewModel.boardDetailList.observe(viewLifecycleOwner) { recentLimitList ->
            binding.recentRecyclerview.adapter = PreviewAdapter(adjustListSize(recentLimitList) , ::navigateToCommunityFragment)
            binding.homeSwipeRefresh.isRefreshing = false
        }
    }

    private fun adjustListSize(list: List<BoardDetail>) =
        if (list.size > PAGE_LOAD_LIMIT)
            list.subList(0, PAGE_LOAD_LIMIT)
        else
            list

    private fun getBoardContentList() {
        communityViewModel.getBoardList()
    }

    private fun navigateToCommunityFragment(boardDetail: BoardDetail) {
        val bundle = bundleOf("boardDetail" to boardDetail)

        requireParentFragment().findNavController()
            .navigate(R.id.action_HomeFragment_to_CommunityFragment, bundle)
    }

    companion object {
        private const val PAGE_LOAD_LIMIT = 4
    }
}