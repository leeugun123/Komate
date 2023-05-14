package org.techtown.kormate.Fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityBoardEditBinding

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private var binding : ActivityBoardEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardEditBinding.inflate(layoutInflater)
        setContentView(binding!!.root)




    }


}