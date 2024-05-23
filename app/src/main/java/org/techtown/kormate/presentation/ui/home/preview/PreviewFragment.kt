package org.techtown.kormate.presentation.ui.home.preview

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
import com.bumptech.glide.Glide
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.IntentCode
import org.techtown.kormate.FragmentCallback
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.presentation.ui.home.board.BoardActivity
import org.techtown.kormate.presentation.ui.home.board.BoardViewModel
import org.techtown.kormate.presentation.ui.home.board.RecentAdapter

class PreviewFragment : Fragment() , FragmentCallback {

    private lateinit var binding : FragmentPreviewBinding
    private val boardViewModel : BoardViewModel by activityViewModels()
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBinding()
        dataUiBinding()
        activityResultLauncherInit()


        binding.homeSwipeRefresh.setOnRefreshListener {
            getBoardList()
        }



    }

    private fun activityResultLauncherInit() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == IntentCode.RESPONSE_CODE_BOARD_SYNC)
                    getBoardList()
            }
    }


    private fun dataUiBinding() {
        recentLimitListObserve()
    }


    private fun uiBinding() {
        profileImgBinding()
        nameBinding()
    }

    private fun nameBinding() {
        binding.userName.text = UserKakaoIntel.userNickName
    }

    private fun profileImgBinding() {

        Glide.with(requireActivity())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.userProfile)

    }

    private fun recentLimitListObserve() {

        boardViewModel.boardDetailList.observe(requireActivity()) { recentLimitList ->
            binding.recentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.recentRecyclerview.adapter = RecentAdapter(limitListSize(recentLimitList) , this)
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

    private fun getBoardList(){
        boardViewModel.getBoardList()
    }

    override fun onNavigateToActivity(boardDetail : BoardDetail) {
        val intent = Intent(requireActivity(), BoardActivity::class.java)
        intent.putExtra(FirebasePathConstant.POST_PATH_INTENT,boardDetail)
        activityResultLauncher.launch(intent)
    }

    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }