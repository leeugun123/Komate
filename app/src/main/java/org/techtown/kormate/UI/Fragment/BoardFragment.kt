package org.techtown.kormate.UI.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.kormate.Constant.FirebasePathConstant
import org.techtown.kormate.FragmentCallback
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.UI.Activity.BoardActivity
import org.techtown.kormate.UI.Adapter.PreviewAdapter
import org.techtown.kormate.UI.Activity.BoardPostActivity
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.databinding.FragmentBoardBinding


class BoardFragment : Fragment() , FragmentCallback{

    private lateinit var binding : FragmentBoardBinding
    private val boardViewModel : BoardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBoardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingApply()
        dataUiBindingInit()

    }

    private fun bindingApply() {

        binding.apply {

            movePost.setOnClickListener {
                startActivityForResult(Intent(activity, BoardPostActivity::class.java), BOARD_REQUEST_CODE)
            }

        }
    }


    private fun dataUiBindingInit() {
        getBoardList()
        recentListObserve()
    }


    private fun recentListObserve() {

        boardViewModel.boardDetailList.observe(requireActivity()) { recentList ->
            binding.boardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.boardRecyclerview.adapter = PreviewAdapter(recentList , this)
        }

    }

    private fun getBoardList(){
        boardViewModel.getBoardList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == BOARD_REQUEST_CODE && resultCode == BOARD_RESPONSE_CODE){
            getBoardList()
        }

    }

    companion object{
        const val BOARD_REQUEST_CODE = 1002
        const val BOARD_RESPONSE_CODE = 1003
    }

    override fun onNavigateToActivity(boardDetail: BoardDetail) {
        val intent = Intent(requireActivity(), BoardActivity::class.java)
        intent.putExtra(FirebasePathConstant.POST_PATH_INTENT,boardDetail)
        startActivityForResult(intent, BOARD_REQUEST_CODE)
    }


}

