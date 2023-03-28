package org.techtown.kormate.Fragment.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.kormate.Fragment.Data.BoardPreview
import org.techtown.kormate.databinding.BoardpreviewBinding

class previewAdapter(private val boardList : ArrayList<BoardPreview>) : RecyclerView.Adapter<previewAdapter.ViewHolder>(){

    class ViewHolder(val binding : BoardpreviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = BoardpreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val list = boardList[position]

        holder.binding.date.text = list.date
        holder.binding.time.text = list.time
        holder.binding.post.text = list.post


    }

    override fun getItemCount(): Int {

        return boardList.size

    }


}