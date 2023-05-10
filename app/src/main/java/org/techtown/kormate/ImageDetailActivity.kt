package org.techtown.kormate

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import org.techtown.kormate.databinding.ActivityImageDetailBinding

class ImageDetailActivity : AppCompatActivity() {

    private var binding : ActivityImageDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val receiveUrl = intent.getStringExtra("imgUrl")
        //imgUrl 받아오기


        val entirePage = intent.getIntExtra("entirePage",3)
        val curPage = intent.getIntExtra("currentPage",1)
        //현재 페이지 / 전체페이지 표시

        binding!!.cur.setText(curPage.toString())
        binding!!.entire.setText(entirePage.toString())
        //페이지 text에 표시

        Glide.with(this)
            .load(receiveUrl)
            .override(1500,1500)
            .centerCrop()
            .into(binding!!.detailImg)


    }


}