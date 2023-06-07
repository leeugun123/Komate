package org.techtown.kormate.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityMajorBinding


class MajorActivity : AppCompatActivity() {

    private var binding : ActivityMajorBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMajorBinding.inflate(layoutInflater)
        setContentView(binding!!.root)



    }


}