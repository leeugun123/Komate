package org.techtown.kormate.UI.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import org.techtown.kormate.FirebasePathConstant.COMMENT_PATH
import org.techtown.kormate.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.FirebasePathConstant.POST_REPORT_PATH
import org.techtown.kormate.FirebasePathConstant.UPLOAD_POST_PATH
import org.techtown.kormate.UI.Adapter.CommentAdapter
import org.techtown.kormate.Util.CurrentDateTime
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Model.Report
import org.techtown.kormate.UI.ViewModel.CommentViewModel
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private val commentViewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }
    private val binding by lazy { ActivityBoardBinding.inflate(layoutInflater) }
    private val receiveData by lazy { intent.getParcelableExtra<BoardDetail>(UPLOAD_POST_PATH) }
    private val commentRecyclerView by lazy { binding.commentRecyclerView }
    private val postId by lazy { tempData.postId }
    private val userId by lazy { tempData.userId }
    private val tempData by lazy { receiveData!!}

    private var commentSize = 0
    private var commentList = listOf<Comment>()
    private val REQUEST_CODE_EDIT_ACTIVITY = 2
    //액티비티 수정


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        boardUiSync()
        //게시판 최신화

        binding.backBtn.setOnClickListener { finish() }

        binding.post.setOnClickListener {

            if(binding.reply.text.isNotEmpty())
                handleComment()
            else
                Toast.makeText(this@BoardActivity, NO_POST_TRY_AGAIN, Toast.LENGTH_SHORT).show()

        } //댓글 등록


        binding.edit.setOnClickListener {
            showPopUpMenu(it)
        }//수정 아이콘


    }

    private fun handleComment() {

        uploadComment()
        binding.reply.text.clear()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.reply.windowToken, 0)

        commentPositionSync()
    }

    private fun showPopUpMenu(view : View) {

        val popUpMenu = PopupMenu(this , view)

        if(userId != tempData.userId)
            popUpMenu.menuInflater.inflate(R.menu.post_report,popUpMenu.menu)
        else
            popUpMenu.menuInflater.inflate(R.menu.post_menu, popUpMenu.menu)

        popUpMenu.setOnMenuItemClickListener { menuItem ->

            when(menuItem.itemId){
                R.id.action_report -> {
                    showReportDialog()
                    true
                }

                R.id.action_delete -> {
                    showDeleteAlertDialog()
                    true
                }

                R.id.action_edit ->{
                    moveBoardEditActivity()
                    true
                }
                else -> false

            }

        }

        popUpMenu.show()
    }

    private fun moveBoardEditActivity() {
        val intent = Intent(this, BoardEditActivity::class.java)
        intent.putExtra(UPLOAD_POST_PATH , receiveData)
        startActivityForResult(intent,REQUEST_CODE_EDIT_ACTIVITY)
    }

    private fun showDeleteAlertDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(REMOVE_POST_COMPLETE)

        builder.setPositiveButton("예") { _, _ ->
            removeBoard()
        }

        builder.setNegativeButton("아니오") { _, _ -> }
        builder.create().show()
    }

    private fun removeBoard() {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
        databaseReference.child(postId).removeValue()
        Toast.makeText(context, "게시물이 삭제 되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun commentPositionSync() {
        commentSize += 1
        commentRecyclerView.scrollToPosition(commentSize-1)
    }

    private fun uploadComment() {

        val objRef = Firebase.database.reference.child(POSTS_PATH).child(postId).child(COMMENT_PATH)

        val id = objRef.push().key.toString()

        val objCommentRef = objRef.child(id)

        val comment = Comment(id, tempData.userId,tempData.userName , tempData.userImg
            ,binding.reply.text.toString() , CurrentDateTime.getCommentTime())

        objCommentRef.setValue(comment)

        Toast.makeText(this@BoardActivity, POST_COMMENT_COMPLETE, Toast.LENGTH_SHORT).show()
    }

    private fun tossIntent(entirePage: Int, curPage: Int,imgUri : String) {

        val intent = Intent(this, ImageDetailActivity::class.java)

        intent.putExtra("entirePage",entirePage)
        intent.putExtra("currentPage",curPage)
        intent.putExtra("imgUrl",imgUri)

        startActivity(intent)
    }

    private fun showReportDialog() {

        val reasons = arrayOf("욕설", "도배", "인종 혐오 표현", "성적인 만남 유도")
        val checkedReasons = booleanArrayOf(false, false, false, false, false)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("신고 사유를 선택하세요")

        builder.setMultiChoiceItems(reasons, checkedReasons) { _, which, isChecked ->
            checkedReasons[which] = isChecked
        }

        builder.setPositiveButton(CHECK) { _, _ ->

            val selectedReasons = mutableListOf<String>()
            for (i in reasons.indices) {

                if (checkedReasons[i]) {
                    selectedReasons.add(reasons[i])
                }

            }

            //선택한 신고 사유들에 대한 처리 진행
            Firebase.database.reference.child(POST_REPORT_PATH).
                        child(Firebase.database.reference.push().key.toString()).setValue(
                Report(userId , selectedReasons , tempData.userId , tempData.postId))

            //신고 넣기
            Toast.makeText(context, REPORT_POST, Toast.LENGTH_SHORT).show()

        }

        builder.setNegativeButton(CANCEL) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun boardUiSync(){

           userInfoUiSync()
           postUiSync()
           commentUiSync()
    }

    private fun commentUiSync() {

        Glide.with(this)
            .load(tempData.userImg)
            .circleCrop()
            .into(binding.replyImg)

        commentViewModel.loadComments(postId)

        commentViewModel.commentLiveData.observe(this) {
            commentAdapterSync(it)
        }

    }

    private fun commentAdapterSync(list : List<Comment>) {
        commentList = list
        commentSize = commentList.size
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = CommentAdapter(commentList, userId, postId)
        commentRecyclerView.scrollToPosition(commentList.size - 1)
    }

    private fun postUiSync() {
        binding.postText.text = tempData.post
        imgUiSync()
    }

    private fun imgUiSync() {

        if(tempData.img.size == 0)
            removeImgView()
        else{
            imgDataSync()
            clickImageView()
        }

    }

    private fun imgDataSync() {

        val imageViewList = listOf(binding.uploadImageView1, binding.uploadImageView2, binding.uploadImageView3)

        for (i in tempData.img.indices) {

            Glide.with(this)
                .load(tempData.img[i])
                .override(1100,1000)
                .into(imageViewList[i])

            imageViewList[i].visibility = View.VISIBLE

        }//사용 하는 imageView 보여 주기

        for (i in tempData.img.size until imageViewList.size) {
            imageViewList[i].visibility = View.GONE
        }//사용 하지 않는 imageView 제거
    }

    private fun clickImageView() {

        binding.uploadImageView1.setOnClickListener {
            tossIntent(tempData.img.size,1, tempData.img[0])
        }

        binding.uploadImageView2.setOnClickListener {
            tossIntent(tempData.img.size,2, tempData.img[1])
        }

        binding.uploadImageView3.setOnClickListener {
            tossIntent(tempData.img.size,3, tempData.img[2])
        }

    }//액티비티로

    private fun removeImgView() {

        val parentView1 = binding.uploadImageView1.parent as ViewGroup
                parentView1.removeView(binding.uploadImageView1)

        val parentView2 = binding.uploadImageView2.parent as ViewGroup
        parentView2.removeView(binding.uploadImageView2)

        val parentView3 = binding.uploadImageView3.parent as ViewGroup
        parentView3.removeView(binding.uploadImageView3)

        binding.commentLinear.setPadding(binding.commentLinear.paddingLeft,
            900, binding.commentLinear.paddingRight ,
            binding.commentLinear.paddingBottom)

    }

    private fun userInfoUiSync() {

        Glide.with(this)
            .load(tempData.userImg)
            .circleCrop()
            .into(binding.userImg)

        binding.userName.text = tempData.userName
        binding.dateTime.text = tempData.dateTime

    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_ACTIVITY && resultCode == Activity.RESULT_OK)
            finish()
    }
    //수정 후 액티비티 종료

    companion object{
        private const val NO_POST_TRY_AGAIN = "글이 없습니다. 다시 작성해주세요."
        private const val REMOVE_POST_COMPLETE = "게시물이 삭제 되었습니다."
        private const val POST_COMMENT_COMPLETE = "댓글이 등록 되었습니다."
        private const val CANCEL = "취소"
        private const val CHECK = "확인"
        private const val REPORT_POST = "게시물이 신고 되었습니다."
    }





}