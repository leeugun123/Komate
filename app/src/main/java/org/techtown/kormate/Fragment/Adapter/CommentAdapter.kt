package org.techtown.kormate.Fragment.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.CommentimgBinding

class CommentAdapter(private val comments : MutableList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

        val binding = CommentimgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {

        val comment = comments[position]
        holder.bind(comment)

    }

    override fun getItemCount(): Int = comments.size

    inner class ViewHolder(private val binding : CommentimgBinding) :

        RecyclerView.ViewHolder(binding.root){


            fun bind(comment : Comment){

                Glide.with(itemView)
                    .load(comment.userImg)
                     .circleCrop()
                    .into(binding.commentUserImg)
                //유저 이미지 추가

                binding.commentUserName.text = comment.userName
                binding.commentTime.text = comment.createdTime
                binding.commentText.text= comment.text



            }


        }

}