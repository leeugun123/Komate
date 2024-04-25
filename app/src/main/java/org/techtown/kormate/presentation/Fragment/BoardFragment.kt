package org.techtown.kormate.presentation.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.kormate.Constant.FirebasePathConstant.POST_PATH_INTENT
import org.techtown.kormate.Constant.IntentCode.RESPONSE_CODE_BOARD_SYNC
import org.techtown.kormate.FragmentCallback
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.presentation.Activity.BoardActivity
import org.techtown.kormate.presentation.Adapter.PreviewAdapter
import org.techtown.kormate.presentation.Activity.BoardPostActivity
import org.techtown.kormate.presentation.ViewModel.BoardViewModel
import org.techtown.kormate.databinding.FragmentBoardBinding


class BoardFragment : Fragment() , FragmentCallback{

    private lateinit var binding : FragmentBoardBinding
    private val boardViewModel : BoardViewModel by activityViewModels()
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBoardBinding.inflate(inflater,container,false)
        return binding.root
    }

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
            binding.boardRecyclerview.adapter = PreviewAdapter(recentList , this)
            binding.boardSwipeRefresh.isRefreshing = false

        }

    }

    private fun getBoardList(){
        boardViewModel.getBoardList()
    }

    override fun onNavigateToActivity(boardDetail: BoardDetail) {
        val intent = Intent(requireActivity(), BoardActivity::class.java)
        intent.putExtra(POST_PATH_INTENT , boardDetail)
        activityResultLauncher.launch(intent)
    }


}

