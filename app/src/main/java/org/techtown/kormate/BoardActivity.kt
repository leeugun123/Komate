package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.techtown.kormate.Fragment.Adapter.CommentAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
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

        if (receiveData != null) {

            val list = receiveData

            if (list != null) {

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

    }


}