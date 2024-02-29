package org.techtown.kormate.UI.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.Constant.IntentCode.REQUEST_CODE_BOARD_SYNC
import org.techtown.kormate.Constant.IntentCode.RESPONSE_CODE_BOARD_SYNC
import org.techtown.kormate.FragmentCallback
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.UI.Activity.BoardActivity
import org.techtown.kormate.UI.Adapter.RecentAdapter
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() ,FragmentCallback {

    private lateinit var binding : FragmentHomeBinding
    private val boardViewModel : BoardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBinding()
        dataUiBinding()

        binding.homeSwipeRefresh.setOnRefreshListener {
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
        binding.userName.text = userNickName
    }

    private fun profileImgBinding() {

        Glide.with(requireActivity())
            .load(userProfileImg)
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

    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_BOARD_SYNC && resultCode == RESPONSE_CODE_BOARD_SYNC){
            getBoardList()
        }

    }

    private fun getBoardList(){
        boardViewModel.getBoardList()
    }

    override fun onNavigateToActivity(boardDetail : BoardDetail) {
        val intent = Intent(requireActivity(), BoardActivity::class.java)
        intent.putExtra(FirebasePathConstant.POST_PATH_INTENT,boardDetail)
        startActivityForResult(intent,REQUEST_CODE_BOARD_SYNC)
    }


}
