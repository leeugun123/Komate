package org.techtown.kormate.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.UI.Adapter.RecentAdapter
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val boardViewModel : BoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBinding()
        dataUiBinding()

        binding.swipeRefreshLayout.setOnRefreshListener {
            getBoardLimitDetailList()
        }

    }

    override fun onStart() {
        super.onStart()
        getBoardLimitDetailList()
    }

    private fun dataUiBinding() {
        getBoardLimitDetailList()
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

        boardViewModel.boardDetailList.observe(viewLifecycleOwner) { recentLimitList ->
            binding.recentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.recentRecyclerview.adapter = RecentAdapter(limitListSize(recentLimitList))
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun limitListSize(list: List<BoardDetail>): List<BoardDetail> {

        return if (list.size > PAGE_LOAD_LIMIT) {
            list.subList(0, PAGE_LOAD_LIMIT)
        } else {
            list
        }

    }

    private fun getBoardLimitDetailList(){
        boardViewModel.getBoardDetailList()
    }

    companion object{
        private const val PAGE_LOAD_LIMIT = 4
    }


}
