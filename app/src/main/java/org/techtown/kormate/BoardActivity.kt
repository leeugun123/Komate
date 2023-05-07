package org.techtown.kormate

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Adapter.CommentAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private var binding : ActivityBoardBinding? = null

    private var commentSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로 가기





        //intent 받기

        val receiveData  = intent.getParcelableExtra<BoardDetail>("postIntel")

        var postId : String? = null
        var userId : Long? = null

        var list : BoardDetail? = null


        val commentRecyclerView = binding!!.commentRecyclerView
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

        if (receiveData != null) {

            list = receiveData

            if (list != null) {

                postId = list.postId

                userId = list.userId

                Glide.with(this)
                    .load(list.userImg)
                    .circleCrop()
                    .into(binding!!.userImg)

                binding!!.userName.setText(list.userName)
                binding!!.dateTime.setText(list.dateTime)



                if(list.img.size == 0){

                    val parentView1 = binding!!.uploadImageView1.parent as ViewGroup
                    parentView1.removeView(binding!!.uploadImageView1)

                    val parentView2 = binding!!.uploadImageView2.parent as ViewGroup
                    parentView2.removeView(binding!!.uploadImageView2)

                    val parentView3 = binding!!.uploadImageView3.parent as ViewGroup
                    parentView3.removeView(binding!!.uploadImageView3)


                    binding!!.commentLinear.setPadding(binding!!.commentLinear.paddingLeft,
                        900, binding!!.commentLinear.paddingRight ,binding!!.commentLinear.paddingBottom)

                }
                else{

                    val imageViewList = listOf(binding!!.uploadImageView1, binding!!.uploadImageView2, binding!!.uploadImageView3)

                    for (i in list.img.indices) {

                        Glide.with(this)
                            .load(list.img[i])
                            .override(1000,1000)
                            .into(imageViewList[i])

                        imageViewList[i].visibility = View.VISIBLE

                    }

                    for (i in list.img.size until imageViewList.size) {
                        imageViewList[i].visibility = View.GONE
                    }

                    //코드가 뭔가 이해가 안 감.....


                }//img가 없을 경우 imgView 제거


                binding!!.postText.setText(list.post)

                Glide.with(this)
                    .load(list.userImg)
                    .circleCrop()
                    .into(binding!!.replyImg)



            }

            val commentsRef = Firebase.database.reference.child("posts").child(postId.toString()).child("comments")

            commentsRef.addValueEventListener(object : ValueEventListener{

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val commentList = mutableListOf<Comment>()

                    for(shapshot in dataSnapshot.children){

                        val comment = shapshot.getValue(Comment::class.java)

                        if(comment != null){
                            commentList.add(comment)
                        }

                    }

                    commentSize = commentList.size
                    commentRecyclerView.adapter = CommentAdapter(commentList , userId!!, postId.toString())
                    commentRecyclerView.scrollToPosition(commentList.size-1)


                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e("TAG","댓글 조회 실패")

                }

            })


        }


        UserApiClient.instance.me { user, error ->

            userId = user?.id

            if(!userId!!.equals(list!!.userId))
                binding!!.edit.visibility = View.GONE

        }//카카오톡을 통해서 사용자 고유 id 가져오기




        binding!!.edit.setOnClickListener {

          val popupMenu = PopupMenu(this,it)

            popupMenu.menuInflater.inflate(R.menu.post_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){

                    R.id.action_delete ->{

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("이 게시판을 삭제하시겠습니까?")

                        builder.setPositiveButton("예") { dialog, which ->

                            val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
                            databaseReference.child(postId.toString()).removeValue()
                            Toast.makeText(context, "게시판이 삭제 되었습니다.", Toast.LENGTH_SHORT).show()

                            finish()

                        }
                        builder.setNegativeButton("아니오") { dialog, which ->

                        }

                        builder.create().show()


                        true
                    }//삭제 기능 구현

                    R.id.action_edit ->{

                        Toast.makeText(context, "편집 기능은 아직 개발 중 입니다. ㅎㅎ", Toast.LENGTH_SHORT).show()






                        true
                    }//편집 기능 선택


                    else -> false
                    //아무것도 선택 x
                }

            }

            popupMenu.show()


        }








        binding!!.post.setOnClickListener {

            if(postId != null){

                Log.e("TAG","등록")

                val objRef = Firebase.database.reference.child("posts").child(postId).child("comments")

                val id = objRef.push().key.toString()

                val objCommentRef = objRef.child(id)

                val comment = Comment(id, list?.userId ,list?.userName ,list?.userImg
                    ,binding!!.reply.text.toString() ,CurrentDateTime.getCommentTime())

                objCommentRef.setValue(comment)

                binding!!.reply.text.clear()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding!!.reply.windowToken, 0)

                Toast.makeText(this@BoardActivity, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show()

                commentSize += 1
                commentRecyclerView.scrollToPosition(commentSize-1)

            }

        }//댓글 등록
        //1개만 등록되지 왜?



    }





}