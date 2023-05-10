package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.kormate.databinding.ActivityImageDetailBinding

class ImageDetailActivity : AppCompatActivity() {

    private var binding : ActivityImageDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)




    }
}