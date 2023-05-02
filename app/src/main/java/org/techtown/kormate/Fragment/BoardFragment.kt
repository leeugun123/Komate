package org.techtown.kormate.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Fragment.Adapter.previewAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.databinding.FragmentBoardBinding


class BoardFragment : Fragment() {

    private var binding : FragmentBoardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentBoardBinding.inflate(layoutInflater)

        binding!!.movePost.setOnClickListener {

            val intent = Intent(activity,BoardPostActivity::class.java)
            startActivity(intent)

        }

        val boardRecyclerView = binding!!.boardRecyclerview
        boardRecyclerView.layoutManager = LinearLayoutManager(activity)

        val postRef = Firebase.database.reference.child("posts")

        val recentList : MutableList<BoardDetail> = mutableListOf()

        //비동기 호출
        postRef.addValueEventListener(object : ValueEventListener{

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
                            post.comments

                            //여기서 comment를 추가해준다.
                        ))

                        Log.e("TAG","프래그먼트 " + post.comments.size.toString())
                        //여기서 comments의 크기가 2로 들어감
                        //중간 데이터를 삭제하는 경우,


                    }

                }

                boardRecyclerView.adapter = previewAdapter(recentList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG",error.toString())
            }


        })







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

