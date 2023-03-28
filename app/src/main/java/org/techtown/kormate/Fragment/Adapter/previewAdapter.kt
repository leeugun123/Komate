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

        var concetPost : String? = null

        if(list.post.toString().length > 105){
            concetPost = list.post?.substring(0,110)+"..."
        }
        else
            concetPost = list.post.toString()

        holder.binding.post.text = concetPost

    }

    override fun getItemCount(): Int {

        return boardList.size

    }


}