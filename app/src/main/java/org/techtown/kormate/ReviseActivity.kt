package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.databinding.ActivityReviseBinding
import org.techtown.kormate.databinding.FragmentMyBinding


class ReviseActivity : AppCompatActivity() {

    private var binding : ActivityReviseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReviseBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        UserApiClient.instance.me { user, _ ->

            "${user?.kakaoAccount?.profile?.nickname}".also {

                if(it != null)
                    binding!!.userName.text = it

            }

            if(user?.kakaoAccount?.profile?.profileImageUrl != null)
                Glide.with(binding!!.userpic).load(user?.kakaoAccount?.profile?.profileImageUrl).circleCrop().into(binding!!.userpic)


        }//내 프로필 사진 카카오 oAuth로 가져오기


        binding!!.backBtn.setOnClickListener {
            finish()
        }

        binding!!.reviseButton.setOnClickListener {

            //파이베이스에 수정된 정보 업로드



            Toast.makeText(this,"수정되었습니다.",Toast.LENGTH_SHORT).show()
            finish()

        }



    }


}