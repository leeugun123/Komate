package org.techtown.kormate.UI.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.kormate.Constant.FirebasePathConstant.COMMENT_PATH
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Constant.FirebasePathConstant.POST_PATH_INTENT
import org.techtown.kormate.UI.Adapter.CommentAdapter
import org.techtown.kormate.Util.CurrentDateTime
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Model.Report
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.UI.ViewModel.CommentViewModel
import org.techtown.kormate.R
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.Util.BoardData
import org.techtown.kormate.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private val commentViewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }
    private val boardViewModel by lazy { ViewModelProvider(this)[BoardViewModel::class.java]}

    private val commentRecyclerView by lazy { binding.commentRecyclerView }

    private val binding by lazy { ActivityBoardBinding.inflate(layoutInflater) }
    private val receiveData by lazy { intent.getParcelableExtra<BoardDetail>(POST_PATH_INTENT)!! }


    private val REQUEST_CODE_EDIT_ACTIVITY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        boardUiSync()
        boardPostIdSync()


        binding.backBtn.setOnClickListener { finish() }

        binding.commentPost.setOnClickListener {

            if(binding.reply.text.isNotEmpty())
                handleComment()
            else
                Toast.makeText(this@BoardActivity, NO_POST_TRY_AGAIN, Toast.LENGTH_SHORT).show()
        }


        binding.edit.setOnClickListener {
            showPopUpMenu(it)
        }

        boardViewModel.boardRemoveSuccess.observe(this){
            if(it){
                Toast.makeText(context, REMOVE_POST_COMPLETE, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        boardViewModel.boardReportSuccess.observe(this){
            if(it)
                Toast.makeText(context, REPORT_POST, Toast.LENGTH_SHORT).show()
        }

        commentViewModel.commentList.observe(this) {commentList ->
            commentAdapterSync(commentList)
        }

        commentViewModel.postCommentSuccess.observe(this){
            if(it)
                Toast.makeText(this@BoardActivity, POST_COMMENT_COMPLETE, Toast.LENGTH_SHORT).show()
        }

    }

    private fun boardPostIdSync() {
        BoardData.boardPostId = receiveData.postId
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
        selectPopupMenu(popUpMenu)
        popUpItemClick(popUpMenu)
        popUpMenu.show()

    }

    private fun popUpItemClick(popupMenu: PopupMenu) {

        popupMenu.setOnMenuItemClickListener { menuItem ->

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
    }

    private fun selectPopupMenu(popupMenu: PopupMenu) {
        if(UserKakaoIntel.userId != receiveData.userId.toString())
            popupMenu.menuInflater.inflate(R.menu.post_report,popupMenu.menu)
        else
            popupMenu.menuInflater.inflate(R.menu.post_menu, popupMenu.menu)
    }

    private fun moveBoardEditActivity() {
        val intent = Intent(this, BoardEditActivity::class.java)
        intent.putExtra(POST_PATH_INTENT , receiveData)
        startActivityForResult(intent,REQUEST_CODE_EDIT_ACTIVITY)
    }

    private fun showDeleteAlertDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(REMOVE_POST_ASKING)

        builder.setPositiveButton("예") { _, _ ->
            removeBoard()
        }

        builder.setNegativeButton("아니오") { _, _ -> }

        builder.create().show()
    }

    private fun removeBoard() {

        lifecycleScope.launch(Dispatchers.Main){
            boardViewModel.removePost(receiveData.postId)
        }
    }

    private fun commentPositionSync() {
        commentSize += 1
        commentRecyclerView.scrollToPosition(commentSize-1)
    }

    private fun uploadComment() {

        val uploadComment = Comment("", UserKakaoIntel.userId.toLong() ,UserKakaoIntel.userNickName , UserKakaoIntel.userProfileImg
            ,binding.reply.text.toString() , CurrentDateTime.getCommentTime())

        commentViewModel.uploadComment(uploadComment , receiveData.postId)

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

            reasons.indices.forEach { idx ->
                if (checkedReasons[idx])
                    selectedReasons.add(reasons[idx])
            }

            userReport(selectedReasons)
        }

        builder.setNegativeButton(CANCEL) { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun userReport(selectedReasons : MutableList<String>) {

        val reportContent = Report(UserKakaoIntel.userId , selectedReasons , receiveData.userId.toString() , receiveData.postId)
        boardViewModel.reportPost(reportContent)
    }


    private fun boardUiSync(){
           userInfoUiSync()
           postUiSync()
           commentUiSync()
    }

    private fun commentUiSync() {
        commentProfileImgBinding()
    }


    private fun commentProfileImgBinding() {
        Glide.with(this)
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.replyImg)
    }

    private fun commentAdapterSync(list : List<Comment>) {
        commentList = list
        commentSize = commentList.size
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = CommentAdapter(commentList, UserKakaoIntel.userId , receiveData.postId , commentViewModel)
        commentRecyclerView.scrollToPosition(commentList.size - 1)
    }

    private fun postUiSync() {
        binding.postText.text = receiveData.post
        imgUiSync()
    }

    private fun imgUiSync() {

        if(receiveData.img.size == 0)
            removeImgView()
        else{
            imgDataSync()
            clickImageView()
        }

    }

    private fun imgDataSync() {

        val imageViewList = listOf(binding.uploadImageView1, binding.uploadImageView2, binding.uploadImageView3)

        for (i in receiveData.img.indices) {

            Glide.with(this)
                .load(receiveData.img[i])
                .override(1100,1000)
                .into(imageViewList[i])

            imageViewList[i].visibility = View.VISIBLE

        }//사용 하는 imageView 보여 주기

        for (i in receiveData.img.size until imageViewList.size) {
            imageViewList[i].visibility = View.GONE
        }//사용 하지 않는 imageView 제거
    }

    private fun clickImageView() {

        binding.uploadImageView1.setOnClickListener {
            tossIntent(receiveData.img.size,1, receiveData.img[0])
        }

        binding.uploadImageView2.setOnClickListener {
            tossIntent(receiveData.img.size,2, receiveData.img[1])
        }

        binding.uploadImageView3.setOnClickListener {
            tossIntent(receiveData.img.size,3, receiveData.img[2])
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
            .load(receiveData.userImg)
            .circleCrop()
            .into(binding.userImg)

        binding.userName.text = receiveData.userName
        binding.dateTime.text = receiveData.dateTime

    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_ACTIVITY && resultCode == Activity.RESULT_OK)
            finish()
    }
    //수정 후 액티비티 종료

    companion object{
        private const val NO_POST_TRY_AGAIN = "글이 없습니다. 다시 작성해주세요."
        private const val REMOVE_POST_ASKING = "게시물을 삭제하시겠습니까?"
        private const val REMOVE_POST_COMPLETE = "게시물이 삭제되었습니다."
        private const val POST_COMMENT_COMPLETE = "댓글이 등록 되었습니다."
        private const val CANCEL = "취소"
        private const val CHECK = "확인"
        private const val REPORT_POST = "게시물이 신고 되었습니다."

        var commentSize = 0
        var commentList = listOf<Comment>()
    }





}