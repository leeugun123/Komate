package org.techtown.kormate.Fragment.Adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.CommentimgBinding


class CommentAdapter(private val comments : MutableList<Comment>, private val userId : Long, private val postId : String) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

        val binding = CommentimgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {

        val comment = comments[position]
        holder.bind(comment,position)

    }

    override fun getItemCount(): Int = comments.size

    inner class ViewHolder(private val binding : CommentimgBinding) :

        RecyclerView.ViewHolder(binding.root){

            val deleteButton : ImageButton = binding.deleteButton

            fun bind(comment : Comment ,pos : Int){

                Glide.with(itemView)
                    .load(comment.userImg)
                     .circleCrop()
                    .into(binding.commentUserImg)
                //유저 이미지 추가

                binding.commentUserName.text = comment.userName
                binding.commentTime.text = comment.createdTime
                binding.commentText.text= comment.text

                Log.e("TAG",comment.userId.toString())

                if(userId == comment.userId){

                    deleteButton.setOnClickListener {

                        val commentsRef = Firebase.database.reference.child("posts").child(postId).child("comments")
                        commentsRef.orderByChild("id").equalTo(comment.id.toString()).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                for (data in snapshot.children) {

                                    data.ref.removeValue().addOnSuccessListener {
                                        // 댓글 삭제 성공 시 comments 리스트에서도 해당 댓글 제거
                                        val comment = data.getValue(Comment::class.java)
                                        comment?.let {

                                            comments.remove(it)
                                            notifyItemRemoved(pos)
                                            notifyItemRangeChanged(pos, comments.size - pos)
                                            Toast.makeText(itemView.context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                                        }
                                    }

                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Error deleting comment: ${error.message}")
                            }


                        })

                    }


                }
                else{
                    deleteButton.visibility = View.GONE
                }




            }




        }

}