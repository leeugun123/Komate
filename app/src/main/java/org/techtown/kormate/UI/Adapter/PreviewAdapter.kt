package org.techtown.kormate.UI.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.UI.Activity.BoardActivity
import org.techtown.kormate.databinding.BoardpreviewBinding

class PreviewAdapter(private val boardList : List<BoardDetail>) : RecyclerView.Adapter<PreviewAdapter.ViewHolder>(){

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

            val concatPost = if(boardDetail.post.length > 105){
                boardDetail.post.substring(0,110)+"..."
            } else
                boardDetail.post

            binding.post.text = concatPost

            binding.root.setOnClickListener {

                val intent = Intent(itemView.context, BoardActivity::class.java)
                intent.putExtra("postIntel",boardDetail)
                itemView.context.startActivity(intent)

            }

        }



    }


}
