package org.techtown.kormate.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var binding : FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentHomeBinding.inflate(layoutInflater)

        UserApiClient.instance.me { user, error ->

            "${user?.kakaoAccount?.profile?.nickname}".also { binding!!.userName.text = it + " 님" }

             Glide.with(binding!!.userProfile).load(user?.kakaoAccount?.profile?.profileImageUrl).circleCrop().into(binding!!.userProfile)


        }//viewModel를 통해 가져오는 것으로 수정




    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        return binding?.root
    }

    override fun onDestroyView() {

        binding = null
        super.onDestroyView()

    }

}