package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Fragment.Adapter.CommentAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private var binding : ActivityBoardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로 가기

        //intent 받기

        val receiveData  = intent.getParcelableExtra<BoardDetail>("postIntel")

        var postId : String? = null

        var list : BoardDetail? = null

        if (receiveData != null) {

            list = receiveData

            if (list != null) {

                postId = list.postId


                Glide.with(this)
                    .load(list.userImg)
                    .circleCrop()
                    .into(binding!!.userImg)



                binding!!.userName.setText(list.userName)
                binding!!.date.setText(list.date)
                binding!!.time.setText(list.time)

                Glide.with(this)
                    .load(list.img)
                    .into(binding!!.uploadImg)

                binding!!.postText.setText(list.post)


                Glide.with(this)
                    .load(list.userImg)
                    .circleCrop()
                    .into(binding!!.replyImg)


                val commentRecyclerView = binding!!.commentRecyclerView
                commentRecyclerView.layoutManager = LinearLayoutManager(this)

                val commentList = list.comments

                commentRecyclerView.adapter = CommentAdapter(commentList)


            }


        }

        val postsRef = Firebase.database.reference.child("posts")
        //댓글 업데이트를 위한 파이베이스 변수

        binding!!.post.setOnClickListener {

            if(postId != null){

                Log.e("TAG","등록")

                val objRef = postsRef.child(postId)

                objRef.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val boardDetail = snapshot.getValue(BoardDetail::class.java)

                        val comment = Comment(list?.userName,list?.userImg
                        ,binding!!.reply.text.toString(),"현재시간")

                        boardDetail!!.comments.add(comment)

                        objRef.setValue(boardDetail)

                    }

                    override fun onCancelled(error: DatabaseError) {


                    }

                })

            }

        }//작성하기




    }


}