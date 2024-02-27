package org.techtown.kormate.UI.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.kormate.UI.Adapter.PreviewAdapter
import org.techtown.kormate.UI.Activity.BoardPostActivity
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.databinding.FragmentBoardBinding


class BoardFragment : Fragment() {

    private lateinit var binding : FragmentBoardBinding
    private val boardViewModel : BoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBoardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.movePost.setOnClickListener {
            startActivity(Intent(activity, BoardPostActivity::class.java))
        }


        dataUiBindingInit()

    }


    private fun dataUiBindingInit() {
        recentListObserve()
    }


    private fun recentListObserve() {

        boardViewModel.boardDetailList.observe(viewLifecycleOwner) { recentList ->
            binding.boardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.boardRecyclerview.adapter = PreviewAdapter(recentList)
        }

    }


}

