package org.techtown.kormate.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var binding : FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentHomeBinding.inflate(layoutInflater)

        UserApiClient.instance.me { user, error ->
            "${user?.kakaoAccount?.profile?.nickname}".also { binding!!.userName.text = it }

            Log.e("TAG","${user?.kakaoAccount?.profile?.nickname}")

        }//카카오에서 닉네임 불러오기

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