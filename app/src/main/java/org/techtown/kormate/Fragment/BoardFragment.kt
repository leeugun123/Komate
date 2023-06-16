package org.techtown.kormate.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Fragment.Adapter.RecentAdapter
import org.techtown.kormate.Fragment.Adapter.previewAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.ViewModel.RecentListModel
import org.techtown.kormate.databinding.FragmentBoardBinding


class BoardFragment : Fragment() {

    private var binding : FragmentBoardBinding? = null

    private lateinit var recentListModel : RecentListModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recentListModel = ViewModelProvider(requireActivity()).get(RecentListModel::class.java)

        recentListModel.loadRecentData(false)
        //limit 개수만큼 가져옴

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBoardBinding.inflate(inflater,container,false)

        binding!!.movePost.setOnClickListener {

            val intent = Intent(activity,BoardPostActivity::class.java)
            startActivity(intent)

        }//게시글 작성


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

        recentListModel.recentList.observe(viewLifecycleOwner) { recentList ->

            binding!!.boardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding!!.boardRecyclerview.adapter = previewAdapter(recentList)

        }


    }


}

