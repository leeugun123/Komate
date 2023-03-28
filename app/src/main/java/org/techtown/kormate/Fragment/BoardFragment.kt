package org.techtown.kormate.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.kormate.Fragment.Adapter.previewAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.BoardPreview
import org.techtown.kormate.R
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

        val recentList = ArrayList<BoardPreview>()

        //비동기 호출
        postRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                recentList.clear()

                for(snapshot in snapshot.children){

                    val post = snapshot.getValue(BoardDetail::class.java)

                    if (post != null) {

                        recentList.add(BoardPreview(post.date.toString(),post.time.toString(),post.post.toString()))

                    }

                }

                boardRecyclerView.adapter = previewAdapter(recentList)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })







    }

    /*
    private fun getData(): ArrayList<BoardPreview> {


        val postRef = Firebase.database.reference.child("posts")

        val recentList = ArrayList<BoardPreview>()

        //비동기 호출
        postRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                recentList.clear()

                for(snapshot in snapshot.children){

                    val post = snapshot.getValue(BoardDetail::class.java)


                    if (post != null) {

                        recentList.add(BoardPreview(post.date.toString(),post.time.toString(),post.post.toString()))


                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        return recentList

    }

    */



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