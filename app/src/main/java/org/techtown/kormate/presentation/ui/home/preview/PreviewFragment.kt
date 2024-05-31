package org.techtown.kormate.presentation.ui.home.preview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentPreviewBinding
import org.techtown.kormate.domain.BoardDetail
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.FragmentCallback
import org.techtown.kormate.presentation.constant.FirebasePathConstant
import org.techtown.kormate.presentation.constant.IntentCode
import org.techtown.kormate.presentation.ui.home.board.detail.BoardActivity
import org.techtown.kormate.presentation.ui.home.board.detail.BoardViewModel

class PreviewFragment : BaseFragment<FragmentPreviewBinding>(R.layout.fragment_preview),
    FragmentCallback {

    private val boardViewModel: BoardViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        observeRecentLimitList()
    }

    private fun initBinding() {
        bindingProfileImg()
        binding.userKakoIntel = UserKakaoIntel
        binding.onRefreshClick = ::getBoardList
    }

    private fun bindingProfileImg() {
        Glide.with(requireContext())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userProfile)
    }

    private fun observeRecentLimitList() {
        boardViewModel.boardDetailList.observe(viewLifecycleOwner) { recentLimitList ->
            binding.recentRecyclerview.adapter = PreviewAdapter(limitListSize(recentLimitList))
            binding.homeSwipeRefresh.isRefreshing = false
        }
    }

    private fun limitListSize(list: List<BoardDetail>) =
        if (list.size > PAGE_LOAD_LIMIT) {
            list.subList(0, PAGE_LOAD_LIMIT)
        } else {
            list
        }

    private fun getBoardList() {
        boardViewModel.getBoardList()
    }

    override fun onNavigateToBoardFragment(boardDetail: BoardDetail) {
        // 인자값 정해야 함.
    }

    companion object {
        private const val PAGE_LOAD_LIMIT = 4
    }
}