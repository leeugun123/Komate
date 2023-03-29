package org.techtown.kormate.Fragment

import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityBoardPostBinding

class BoardPostActivity : AppCompatActivity() {

    private var binding : ActivityBoardPostBinding? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postsRef = Firebase.database.reference.child("posts")
        val postId = postsRef.push().key

        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)



        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로가기

        var userName : String? = null
        var userImg: String? = null

        UserApiClient.instance.me { user, error ->

            userName = user?.kakaoAccount?.profile?.nickname
            userImg = user?.kakaoAccount?.profile?.profileImageUrl

        }//viewModel를 통해 가져오는 것으로 수정

        binding!!.uploadImgButton.setOnClickListener {




        }//사진 올리기



        binding!!.updateButton.setOnClickListener {

            val post = binding!!.post.text.toString()

            val date : String = getDate()
            val time : String = getTime()

            val boardPost = BoardDetail(userName,userImg,post,"img",date,time)


            postId?.let{
                postsRef.child(it).setValue(boardPost)
            }

            Toast.makeText(this,"게시글이 등록되었습니다.",Toast.LENGTH_SHORT).show()

            finish()

        }//업데이트 버튼


    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDate() : String{

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateString = "$year-${month + 1}-$day"   // 2022-4-22

        return dateString

    }//현재 날짜 가져오기

    @RequiresApi(Build.VERSION_CODES.N)
    fun getTime() : String{

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        var timeString : String? = null

        if(minute.toString().length <= 1)
            timeString = "$hour" +":"+ "0" +"$minute"
        else
            timeString = "$hour:$minute"

        return timeString

    }//현재 시간 가져오기


}