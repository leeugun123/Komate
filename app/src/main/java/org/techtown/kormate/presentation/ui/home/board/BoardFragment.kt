package org.techtown.kormate.presentation.ui.home.board

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentBoardBinding
import org.techtown.kormate.domain.BoardDetail
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.FragmentCallback
import org.techtown.kormate.presentation.constant.FirebasePathConstant.POST_PATH_INTENT
import org.techtown.kormate.presentation.constant.IntentCode.RESPONSE_CODE_BOARD_SYNC
import org.techtown.kormate.presentation.ui.home.board.detail.BoardActivity
import org.techtown.kormate.presentation.ui.home.board.detail.BoardViewModel
import org.techtown.kormate.presentation.ui.home.board.detail.post.BoardPostActivity
import org.techtown.kormate.presentation.ui.home.preview.PreviewAdapter


class BoardFragment : BaseFragment<FragmentBoardBinding>(R.layout.fragment_board),
    FragmentCallback {

    private val boardViewModel: BoardViewModel by viewModels({ requireParentFragment() })
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingApply()
        dataUiBindingInit()
        activityResultLauncherInit()
    }

    private fun activityResultLauncherInit() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESPONSE_CODE_BOARD_SYNC)
                    getBoardList()
            }
    }

    private fun bindingApply() {

        binding.apply {
            movePost.setOnClickListener {
                activityResultLauncher.launch(Intent(activity, BoardPostActivity::class.java))
            }
            boardSwipeRefresh.setOnRefreshListener {
                getBoardList()
            }
        }
    }


    private fun dataUiBindingInit() {
        getBoardList()
        recentListObserve()
    }


    private fun recentListObserve() {

        boardViewModel.boardDetailList.observe(requireActivity()) { recentList ->
            binding.boardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.boardRecyclerview.adapter = PreviewAdapter(recentList)
            binding.boardSwipeRefresh.isRefreshing = false
        }

    }

    private fun getBoardList() {
        boardViewModel.getBoardList()
    }

    override fun onNavigateToActivity(boardDetail: BoardDetail) {
        val intent = Intent(requireActivity(), BoardActivity::class.java)
        intent.putExtra(POST_PATH_INTENT, boardDetail)
        activityResultLauncher.launch(intent)
    }
}

