package org.techtown.kormate.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.UI.Adapter.RecentAdapter
import org.techtown.kormate.UI.ViewModel.KakaoViewModel
import org.techtown.kormate.UI.ViewModel.RecentListModel
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val recentListModel by lazy { ViewModelProvider(requireActivity())[RecentListModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentListObserve()
    }

    private fun recentListObserve() {

        recentListModel.loadRecentData(true) //limit 개수만큼 가져옴

        recentListModel.recentList.observe(viewLifecycleOwner) { recentList ->
            binding.recentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.recentRecyclerview.adapter = RecentAdapter(recentList)
        }

    }



}
