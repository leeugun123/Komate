package org.techtown.kormate

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
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
import org.techtown.kormate.Fragment.BoardEditActivity
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private val REQUEST_CODE_EDIT_ACTIVITY = 2
    //액티비티 수정

    private var binding : ActivityBoardBinding? = null
    private var commentSize : Int = 0

    private var postId : String? = null
    private var userId : Long? = null
    private var list : BoardDetail? = null
    private var commentRecyclerView : RecyclerView? = null
    private var commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로 가기

        commentRecyclerView = binding!!.commentRecyclerView
        commentRecyclerView!!.layoutManager = LinearLayoutManager(this)

        //intent 받기
        val receiveData  = intent.getParcelableExtra<BoardDetail>("postIntel")

        postBoardDetail(receiveData!!)
        //게시판 최신화


        binding!!.post.setOnClickListener {

            if(postId != null){

                val objRef = Firebase.database.reference.child("posts").child(postId!!).child("comments")

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
                commentRecyclerView!!.scrollToPosition(commentSize-1)

            }

        }
        //댓글 등록


        UserApiClient.instance.me { user, _ ->

            userId = user?.id

            if(userId!! != list!!.userId)
                binding!!.edit.visibility = View.GONE

        }//userId를 통해 확인하여 수정 아이콘 view 확인


        binding!!.edit.setOnClickListener {

            val popupMenu = PopupMenu(this,it)

            popupMenu.menuInflater.inflate(R.menu.post_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when(menuItem.itemId){

                    R.id.action_delete -> {

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("이 게시물을 삭제하시겠습니까?")

                        builder.setPositiveButton("예") { _, _ ->

                            val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
                            databaseReference.child(postId.toString()).removeValue()
                            Toast.makeText(context, "게시물이 삭제 되었습니다.", Toast.LENGTH_SHORT).show()

                            finish()

                        }
                        builder.setNegativeButton("아니오") { _, _ ->

                        }

                        builder.create().show()

                        true
                    }//삭제 기능 구현

                    R.id.action_edit ->{

                        val intent = Intent(this,BoardEditActivity::class.java)
                        intent.putExtra("postIntel",receiveData)



                        startActivityForResult(intent,REQUEST_CODE_EDIT_ACTIVITY)

                        true
                    }//편집 기능 선택


                    else -> false
                    //아무것도 선택 x
                }

            }

            popupMenu.show()

        }//수정 아이콘






    }

    private fun tossIntent(entirePage: Int, curPage: Int,imgUri : String) {

        val intent = Intent(this,ImageDetailActivity::class.java)

        intent.putExtra("entirePage",entirePage)
        intent.putExtra("currentPage",curPage)
        intent.putExtra("imgUrl",imgUri)

        startActivity(intent)

    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_ACTIVITY && resultCode == Activity.RESULT_OK) {

            val receive_Intent  = data?.getParcelableExtra<BoardDetail>("resIntent")

            val board : BoardDetail? = receive_Intent

            postBoardDetail(board!!)


        }

    }//수정하고 난 후 최신화

    private fun postBoardDetail(receiveIntent : BoardDetail){


        if (receiveIntent != null) {

            list = receiveIntent

            if (list != null) {

                postId = list!!.postId

                userId = list!!.userId

                Glide.with(this)
                    .load(list!!.userImg)
                    .circleCrop()
                    .into(binding!!.userImg)

                binding!!.userName.text = list!!.userName
                binding!!.dateTime.text = list!!.dateTime

                if(list!!.img.size == 0){

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

                    for (i in list!!.img.indices) {

                        Glide.with(this)
                            .load(list!!.img[i])
                            .override(1000,1000)
                            .into(imageViewList[i])

                        imageViewList[i].visibility = View.VISIBLE

                    }

                    for (i in list!!.img.size until imageViewList.size) {
                        imageViewList[i].visibility = View.GONE
                    }


                    binding!!.uploadImageView1.setOnClickListener {
                        tossIntent(list!!.img.size,1, list!!.img[0])
                    }//첫번째 뷰

                    binding!!.uploadImageView2.setOnClickListener {
                        tossIntent(list!!.img.size,2, list!!.img[1])
                    }//두번째 뷰

                    binding!!.uploadImageView3.setOnClickListener {
                        tossIntent(list!!.img.size,3, list!!.img[2])
                    }//세번째 뷰


                }//img가 없을 경우 imgView 제거

                binding!!.postText.text = list!!.post

                Glide.with(this)
                    .load(list!!.userImg)
                    .circleCrop()
                    .into(binding!!.replyImg)

                val commentsRef = Firebase.database.reference.child("posts").child(postId.toString()).child("comments")

                commentsRef.addValueEventListener(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        commentList.clear()

                        for(shapshot in dataSnapshot.children){

                            val comment = shapshot.getValue(Comment::class.java)

                            if(comment != null){
                                commentList.add(comment)
                                Log.e("TAG","댓글 조회")
                            }

                        }

                        Log.e("TAG","댓글 변경 감지")
                        commentSize = commentList.size
                        commentRecyclerView!!.adapter = CommentAdapter(commentList , userId!!, postId.toString())
                        commentRecyclerView!!.scrollToPosition(commentList.size-1)


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG","댓글 조회 실패")
                    }

                })//댓글 최신화

            }//게시판 최신화


        }


    }





}