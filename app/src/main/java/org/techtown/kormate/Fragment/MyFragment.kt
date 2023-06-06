package org.techtown.kormate.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentMyBinding


class MyFragment : Fragment() {

    private var binding : FragmentMyBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentMyBinding.inflate(layoutInflater)

        UserApiClient.instance.me { user, error ->

            "${user?.kakaoAccount?.profile?.nickname}".also {

                if(it != null)
                    binding!!.userName.text = it

            }

            if(user?.kakaoAccount?.profile?.profileImageUrl != null)
                Glide.with(binding!!.userProfile).load(user?.kakaoAccount?.profile?.profileImageUrl).circleCrop().into(binding!!.userProfile)


        }//내 정보 카카오 oAuth로 가져오기




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding?.root
    }

    override fun onDestroyView() {

        binding = null
        super.onDestroyView()

    }


}