package org.techtown.kormate.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.LoginActivity
import org.techtown.kormate.R
import org.techtown.kormate.ReviseActivity
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

        binding!!.logoutButton.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("로그아웃 하시겠습니까?")

            builder.setPositiveButton("예") { dialog, which ->

                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        // 로그아웃 실패 처리
                        Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    } else {

                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)

                        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                        requireActivity().finish()

                    }
                }


            }

            builder.setNegativeButton("아니오") { dialog, which ->

                dialog.dismiss()

            }

            val dialog = builder.create()
            dialog.show()


        }

        binding!!.reviseButt.setOnClickListener {

            val intent = Intent(requireContext(), ReviseActivity::class.java)
            startActivity(intent)

        }//수정 버튼








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