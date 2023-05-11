package org.techtown.kormate.Fragment.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.BoardActivity
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.databinding.RecentpreviewBinding


class RecentAdapter(private val boardList : List<BoardDetail>) : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : RecentpreviewBinding) : RecyclerView.ViewHolder(binding.root),
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapter.ViewHolder {

        val binding = RecentpreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecentAdapter.ViewHolder, position: Int) {

        val list = boardList[position]

        holder.binding.dateTime.text = list.dateTime

        var concetPost : String? = null

        if(list.post.toString().length > 53){
            concetPost = list.post?.substring(0,52)+"..."
        }
        else
            concetPost = list.post.toString()

        holder.binding.post.text = concetPost

        holder.binding.userName.text = list.userName

        Glide.with(holder.itemView.context)
            .load(list.userImg)
            .override(100,100)
            .circleCrop()
            .into(holder.binding.userImg)

    }

    override fun getItemCount(): Int {
        return boardList.size
    }


}