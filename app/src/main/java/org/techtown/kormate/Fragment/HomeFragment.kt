package org.techtown.kormate.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.techtown.kormate.Fragment.Adapter.RecentAdapter
import org.techtown.kormate.Fragment.ViewModel.KakaoViewModel
import org.techtown.kormate.Fragment.ViewModel.RecentListModel
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val kakaoViewModel by lazy { ViewModelProvider(requireActivity())[KakaoViewModel::class.java] }
    private val recentListModel by lazy { ViewModelProvider(requireActivity())[RecentListModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kakaoViewModel.loadUserData()
        recentListModel.loadRecentData(true) //limit 개수만큼 가져옴

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }

    private fun observeViewModel() {
        kakaoObserve()
        fireBaseObserve()
    }

    private fun fireBaseObserve() {
        recentListModel.recentList.observe(viewLifecycleOwner) { recentList ->
            binding.recentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.recentRecyclerview.adapter = RecentAdapter(recentList)
        }
    }

    private fun kakaoObserve() {

        kakaoViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.userName.text = "$userName 님"
        }

        kakaoViewModel.userProfileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(binding.userProfile).load(imageUrl).circleCrop().into(binding.userProfile)
        }

    }


}
