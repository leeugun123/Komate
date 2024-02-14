package org.techtown.kormate.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import org.techtown.kormate.Constant.FirebasePathConstant.USER_INTEL_PATH
import org.techtown.kormate.Model.UserIntel
import org.techtown.kormate.Model.UserKakaoIntel.userId
import org.techtown.kormate.UI.ViewModel.MyIntelViewModel
import org.techtown.kormate.databinding.ActivityReviseBinding


class ReviseActivity : AppCompatActivity() {

    private val binding by lazy { ActivityReviseBinding.inflate(layoutInflater) }
    private val myIntelViewModel by lazy { ViewModelProvider(this)[MyIntelViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.selfEdittext.setText(UserIntel.selfIntro)
        binding.majorIntel.setText(UserIntel.major)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.reviseButton.setOnClickListener {

            UserIntel.selfIntro = binding.selfEdittext.text.toString()
            UserIntel.major = binding.majorIntel.text.toString()
            writeIntelFirebase()
        }


        myIntelViewModel.postLiveData.observe(this){ success ->

            if(success){
                Toast.makeText(this, REVISE_COMPLETE_MESSAGE ,Toast.LENGTH_SHORT).show()
                finish()
            }

        }



    }

    private fun writeIntelFirebase() {
        myIntelViewModel.uploadUserIntel(UserIntel)
    }


    companion object{
        private const val REVISE_COMPLETE_MESSAGE = "수정 되었습니다."
    }



}