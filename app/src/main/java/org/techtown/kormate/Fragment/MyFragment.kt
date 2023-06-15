package org.techtown.kormate.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Adapter.RecentAdapter
import org.techtown.kormate.Fragment.Data.UserIntel
import org.techtown.kormate.Fragment.ViewModel.KakaoViewModel
import org.techtown.kormate.Fragment.ViewModel.MyIntelModel
import org.techtown.kormate.LoginActivity
import org.techtown.kormate.R
import org.techtown.kormate.ReviseActivity
import org.techtown.kormate.databinding.FragmentMyBinding


class MyFragment : Fragment() {

    private var binding : FragmentMyBinding? = null

    private var userIntel : UserIntel? = null
    private var userId : String? = null

    companion object {
        const val REQUEST_REVISE = 1
    }

    lateinit var kakaoViewModel : KakaoViewModel
    lateinit var myIntelModel : MyIntelModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kakaoViewModel = ViewModelProvider(requireActivity()).get(KakaoViewModel::class.java)
        myIntelModel = ViewModelProvider(requireActivity()).get(MyIntelModel::class.java)

        userId = kakaoViewModel.userId

        kakaoViewModel.loadUserData()
        //꼭 있어야 하나?
        myIntelModel.fetchUserIntel(userId.toString())





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyBinding.inflate(inflater,container,false)

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
            startActivityForResult(intent, REQUEST_REVISE)

        }//수정 버튼


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }

    override fun onDestroyView() {

        binding = null
        super.onDestroyView()

    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {

        kakaoViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding?.userName?.text = userName
        }

        kakaoViewModel.userProfileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(binding?.userProfile!!).load(imageUrl).circleCrop().into(binding?.userProfile!!)
        }

        myIntelModel.userIntel.observe(viewLifecycleOwner){ userIntel ->

            //UI 업데이트
            binding?.selfMajor?.text = "서울과학기술대학교 | ${userIntel.major}"
            binding?.selfIntroText?.text = userIntel.selfIntro
            binding?.majorText?.text = userIntel.major

            var nation : String?

            if(userIntel!!.nation.toString() == "한국"){
                nation = "Korea"
            }else if(userIntel!!.nation.toString() == "중국"){
                nation = "China"
            }
            else if(userIntel!!.nation.toString() == "베트남"){
                nation = "Vietnam"
            }
            else if(userIntel!!.nation.toString() == "몽골"){
                nation = "Mongolia"
            }else
                nation = "Uzbekistan"

            binding!!.nation.text = nation

            binding!!.nationText.text = userIntel.nation.toString()
            binding!!.genderText.text = userIntel.gender.toString()

        }




    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_REVISE) {
            if (resultCode == Activity.RESULT_OK) {

                val response = data?.getParcelableExtra<UserIntel>("userIntel")

                binding!!.selfMajor.text = "서울과학기술대학교 | " + response!!.major.toString()

                binding!!.selfIntroText.text = response!!.selfIntro.toString()

                binding!!.majorText.text = response!!.major.toString()

            }

        }


    }

}