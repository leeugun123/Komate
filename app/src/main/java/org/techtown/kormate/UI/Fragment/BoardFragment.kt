package org.techtown.kormate.UI.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.kormate.UI.Adapter.PreviewAdapter
import org.techtown.kormate.UI.Activity.BoardPostActivity
import org.techtown.kormate.UI.ViewModel.RecentListViewModel
import org.techtown.kormate.databinding.FragmentBoardBinding


class BoardFragment : Fragment() {

    private lateinit var binding : FragmentBoardBinding
    private val recentListViewModel by lazy { ViewModelProvider(requireActivity())[RecentListViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBoardBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.movePost.setOnClickListener {
            startActivity(Intent(activity, BoardPostActivity::class.java))
        }

        getRecentBoardData()
        observeViewModel()

    }

    private fun getRecentBoardData() {

        lifecycleScope.launch(Dispatchers.Main){
            recentListViewModel.loadRecentData(false)
        }

    }

    private fun observeViewModel() {

        recentListViewModel.recentList.observe(viewLifecycleOwner) { recentList ->

            binding.boardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.boardRecyclerview.adapter = PreviewAdapter(recentList)

        }

    }


}

