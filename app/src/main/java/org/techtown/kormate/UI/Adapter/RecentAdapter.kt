package org.techtown.kormate.UI.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.UI.Activity.BoardActivity
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.databinding.RecentpreviewBinding


class RecentAdapter(private val boardList : List<BoardDetail>) : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : RecentpreviewBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener{

        init { itemView.setOnClickListener(this) }

        override fun onClick(v : View){
            val intent = Intent(itemView.context, BoardActivity::class.java)
            intent.putExtra("postIntel",boardList[adapterPosition])
            itemView.context.startActivity(intent)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(RecentpreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        syncAdapterUi(boardList[position] , holder)
    }

    private fun syncAdapterUi(list : BoardDetail , holder : ViewHolder) {

        holder.binding.dateTime.text = list.dateTime

        holder.binding.post.text = if(list.post.toString().length > 53){
            list.post?.substring(0,52)+"..."
        } else
            list.post.toString()

        holder.binding.userName.text = list.userName

        Glide.with(holder.itemView.context)
            .load(list.userImg)
            .override(profileWidthSize, profileHeightSize)
            .circleCrop()
            .into(holder.binding.userImg)

    }

    override fun getItemCount() = boardList.size

    companion object{
        private const val profileWidthSize = 100
        private const val profileHeightSize = 100
    }



}