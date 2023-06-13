package org.techtown.kormate.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Adapter.RecentAdapter
import org.techtown.kormate.Fragment.Adapter.previewAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.ViewModel.HomeViewModel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        viewModel.loadUserData()
        viewModel.loadRecentData()

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

        viewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding?.userName?.text = userName
        }

        viewModel.userProfileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(binding?.userProfile!!).load(imageUrl).circleCrop().into(binding?.userProfile!!)
        }

        viewModel.recentList.observe(viewLifecycleOwner) { recentList ->

            Log.e("TAG",recentList.get(0).userName.toString())
            //데이터는 정상적으로 가져옴

            binding?.recentRecyclerview?.adapter = RecentAdapter(recentList)

        }
    }
}
