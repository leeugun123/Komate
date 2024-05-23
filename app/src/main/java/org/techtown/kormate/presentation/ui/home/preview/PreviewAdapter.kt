package org.techtown.kormate.presentation.ui.home.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.kormate.domain.BoardDetail
import org.techtown.kormate.databinding.BoardpreviewBinding
import org.techtown.kormate.presentation.ui.home.board.BoardFragment

class PreviewAdapter(private val boardList : List<BoardDetail>, private val boardFragment : BoardFragment) : RecyclerView.Adapter<PreviewAdapter.ViewHolder>(){

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
