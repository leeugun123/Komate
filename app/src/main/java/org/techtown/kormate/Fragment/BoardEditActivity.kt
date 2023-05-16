package org.techtown.kormate.Fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityBoardPostBinding

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private var binding : ActivityBoardPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.title.text = "게시물 수정"
        binding!!.updateButton.text = "수정하기"







    }


}