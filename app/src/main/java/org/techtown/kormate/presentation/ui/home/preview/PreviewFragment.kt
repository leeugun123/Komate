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
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initActivityResultLauncher()

        observeRecentLimitList()

        binding.homeSwipeRefresh.setOnRefreshListener {
            getBoardList()
        }
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == IntentCode.RESPONSE_CODE_BOARD_SYNC)
                    getBoardList()
            }
    }

    private fun initBinding() {
        bindingProfileImg()
        bindingName()
    }

    private fun bindingName() {
        binding.userName.text = UserKakaoIntel.userNickName
    }

    private fun bindingProfileImg() {
        Glide.with(requireActivity())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userProfile)
    }

    private fun observeRecentLimitList() {
        boardViewModel.boardDetailList.observe(viewLifecycleOwner) { recentLimitList ->
            binding.recentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.recentRecyclerview.adapter = RecentAdapter(limitListSize(recentLimitList))
            binding.homeSwipeRefresh.isRefreshing = false
        }
    }

    private fun limitListSize(list: List<BoardDetail>): List<BoardDetail> {
        return if (list.size > PAGE_LOAD_LIMIT) {
            list.subList(0, PAGE_LOAD_LIMIT)
        } else {
            list
        }
    }

    private fun getBoardList() {
        boardViewModel.getBoardList()
    }

    override fun onNavigateToActivity(boardDetail: BoardDetail) {
        val intent = Intent(requireActivity(), BoardActivity::class.java)
        intent.putExtra(FirebasePathConstant.POST_PATH_INTENT, boardDetail)
        activityResultLauncher.launch(intent)
    }

    companion object {
        private const val PAGE_LOAD_LIMIT = 4
    }
}