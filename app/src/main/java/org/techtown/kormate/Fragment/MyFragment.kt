package org.techtown.kormate.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.UserIntel
import org.techtown.kormate.LoginActivity
import org.techtown.kormate.R
import org.techtown.kormate.ReviseActivity
import org.techtown.kormate.databinding.FragmentMyBinding


class MyFragment : Fragment() {

    private var binding : FragmentMyBinding? = null

    private var userIntel : UserIntel? = null

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


            val database = FirebaseDatabase.getInstance()
            val reference = database.reference.child("usersIntel").child(user!!.id.toString())

            reference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    userIntel = dataSnapshot.getValue(UserIntel::class.java)

                    var nation : String? = null

                    if(userIntel!!.nation.toString() == "한국"){
                        nation = "Korea"
                    }else if(userIntel!!.nation.toString() == "중국"){
                        nation = "China"
                    }
                    else if(userIntel!!.nation.toString() == "중국"){
                        nation = "Vietnam"
                    }
                    else if(userIntel!!.nation.toString() == "중국"){
                        nation = "Mongolia"
                    }else
                        nation = "Uzbekistan"

                    binding!!.selfMajor.text = "서울과학기술대학교 | " + userIntel!!.major.toString()

                    binding!!.nation.text = nation

                    binding!!.selfIntroText.text = userIntel!!.selfIntro.toString()

                    binding!!.majorText.text = userIntel!!.major.toString()
                    binding!!.nationText.text = userIntel!!.nation.toString()
                    binding!!.genderText.text = userIntel!!.gender.toString()

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }

            })


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
            intent.putExtra("userIntel",userIntel)
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