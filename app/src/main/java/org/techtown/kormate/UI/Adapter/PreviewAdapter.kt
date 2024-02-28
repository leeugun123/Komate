package org.techtown.kormate.UI.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.UI.Activity.BoardActivity
import org.techtown.kormate.UI.Fragment.BoardFragment
import org.techtown.kormate.UI.Fragment.HomeFragment
import org.techtown.kormate.databinding.BoardpreviewBinding

class PreviewAdapter(private val boardList : List<BoardDetail> , private val boardFragment : BoardFragment) : RecyclerView.Adapter<PreviewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = BoardpreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }


    override fun getItemCount() = boardList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(boardList[position])
    }


    inner class ViewHolder(val binding : BoardpreviewBinding) :

        RecyclerView.ViewHolder(binding.root){

        fun bind(boardDetail : BoardDetail){

            binding.dateTime.text = boardDetail.dateTime

            val concatPost = if(boardDetail.post.length > PREVIEW_TEXT_CUT_MAX_SIZE){
                boardDetail.post.substring(PREVIEW_TEXT_CUT_START, PREVIEW_TEXT_CUT_END) + "..."
            } else
                boardDetail.post

            binding.post.text = concatPost

            binding.root.setOnClickListener {
                boardFragment.onNavigateToActivity(boardDetail)
            }

        }




    }

    companion object{
        private const val PREVIEW_TEXT_CUT_START = 0
        private const val PREVIEW_TEXT_CUT_END = 110
        private const val PREVIEW_TEXT_CUT_MAX_SIZE = 105
    }


}
