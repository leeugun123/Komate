package org.techtown.kormate.Fragment.Adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.CurrentDateTime
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.CommentimgBinding


class CommentAdapter(private val comments : MutableList<Comment>, private val userId : Long, private val postId : String) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

        val binding = CommentimgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {

       holder.bind(comments[position])

    }


    override fun getItemCount(): Int = comments.size

    inner class ViewHolder(private val binding : CommentimgBinding) :

        RecyclerView.ViewHolder(binding.root){

            val deleteButton : ImageButton = binding.deleteButton

            fun bind(comment : Comment){

                Glide.with(itemView)
                    .load(comment.userImg)
                     .circleCrop()
                    .into(binding.commentUserImg)
                //유저 이미지 추가

                binding.commentUserName.text = comment.userName
                binding.commentTime.text = comment.createdTime
                binding.commentText.text= comment.text


                if(userId == comment.userId){

                    deleteButton.setOnClickListener {

                        val builder = AlertDialog.Builder(binding.root.context)
                        builder.setTitle("댓글을 삭제하시겠습니까?")

                        builder.setPositiveButton("예") { dialog, which ->

                            val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
                                .child(postId).child("comments")

                            databaseReference.child(comment.id.toString()).removeValue()

                            Toast.makeText(binding.root.context, " 댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show()

                        }
                        builder.setNegativeButton("아니오") { dialog, which ->


                        }

                        builder.create().show()


                    }


                }
                else{
                    deleteButton.visibility = View.GONE
                }




            }




        }

}