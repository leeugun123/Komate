package org.techtown.kormate.Fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityBoardEditBinding

class BoardEditActivity : AppCompatActivity() {

    private var binding : ActivityBoardEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardEditBinding.inflate(layoutInflater)
        setContentView(binding!!.root)




    }


}