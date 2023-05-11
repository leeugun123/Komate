package org.techtown.kormate.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Adapter.RecentAdapter
import org.techtown.kormate.Fragment.Adapter.previewAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var binding : FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentHomeBinding.inflate(layoutInflater)

        UserApiClient.instance.me { user, error ->

            "${user?.kakaoAccount?.profile?.nickname}".also {

                if(it != null)
                    binding!!.userName.text = it + " 님"

            }

            if(user?.kakaoAccount?.profile?.profileImageUrl != null)
                Glide.with(binding!!.userProfile).load(user?.kakaoAccount?.profile?.profileImageUrl).circleCrop().into(binding!!.userProfile)


        }//viewModel를 통해 가져오는 것으로 수정


        val recentRecyclerView = binding!!.recentRecyclerview
        recentRecyclerView.layoutManager = LinearLayoutManager(activity)

        val postRef = Firebase.database.reference.child("posts")

        val recentList : MutableList<BoardDetail> = mutableListOf()

        //비동기 호출
        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                recentList.clear()

                for(snapshot in snapshot.children){

                    val post = snapshot.getValue(BoardDetail::class.java)

                    if (post != null) {

                        recentList.add(BoardDetail(

                            post.postId,
                            post.userId,
                            post.userName,
                            post.userImg,
                            post.post,
                            post.img,
                            post.dateTime,

                            //여기서 comment를 추가해준다.
                        ))

                        if(recentList.size == 4)
                            break
                        //4개까지만 가져오기


                    }

                }

                recentList.reverse()

                recentRecyclerView.adapter = RecentAdapter(recentList)


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG",error.toString())
            }


        })






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