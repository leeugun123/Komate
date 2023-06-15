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

    private var binding: FragmentHomeBinding? = null
    private lateinit var kakaoViewModel : KakaoViewModel
    private lateinit var recentListModel : RecentListModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kakaoViewModel = ViewModelProvider(requireActivity()).get(KakaoViewModel::class.java)
        recentListModel = ViewModelProvider(requireActivity()).get(RecentListModel::class.java)

        kakaoViewModel.loadUserData()
        recentListModel.loadRecentData()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun observeViewModel() {

        kakaoViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding?.userName?.text = userName
        }

        kakaoViewModel.userProfileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(binding?.userProfile!!).load(imageUrl).circleCrop().into(binding?.userProfile!!)
        }

        recentListModel.recentList.observe(viewLifecycleOwner) { recentList ->

            binding?.recentRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
            binding?.recentRecyclerview?.adapter = RecentAdapter(recentList)

        }


    }
}
