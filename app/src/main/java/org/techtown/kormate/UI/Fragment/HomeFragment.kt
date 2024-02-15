package org.techtown.kormate.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.UI.Adapter.RecentAdapter
import org.techtown.kormate.UI.ViewModel.RecentListViewModel
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val recentListViewModel by lazy { ViewModelProvider(requireActivity())[RecentListViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBinding()
        recentListObserve()
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

    private fun recentListObserve() {


        recentListViewModel.loadRecentData(true) //limit 개수만큼 가져옴

        recentListViewModel.recentList.observe(viewLifecycleOwner) { recentList ->
            Log.e("TAG",recentList.size.toString() + "  " + "사이즈의 개수")
            binding.recentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.recentRecyclerview.adapter = RecentAdapter(recentList)
        }

    }



}
