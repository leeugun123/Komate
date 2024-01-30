package org.techtown.kormate.UI.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.kormate.UI.Activity.BoardActivity
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.databinding.BoardpreviewBinding

class previewAdapter(private val boardList : List<BoardDetail>) : RecyclerView.Adapter<previewAdapter.ViewHolder>(){

    inner class ViewHolder(val binding : BoardpreviewBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener{

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v : View){

                val position = adapterPosition
                val context = itemView.context

                val intent = Intent(context, BoardActivity::class.java)
                intent.putExtra("postIntel",boardList[position])
                context.startActivity(intent)


            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = BoardpreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)


    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val list = boardList[position]

        holder.binding.dateTime.text = list.dateTime

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