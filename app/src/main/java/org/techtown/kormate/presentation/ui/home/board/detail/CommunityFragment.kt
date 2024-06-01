package org.techtown.kormate.presentation.ui.home.board.detail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentCommunityBinding
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.domain.model.Comment
import org.techtown.kormate.domain.model.Report
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.home.board.detail.comment.CommentAdapter
import org.techtown.kormate.presentation.ui.home.board.detail.comment.CommentViewModel
import org.techtown.kormate.presentation.ui.showToast
import org.techtown.kormate.presentation.util.BoardData
import org.techtown.kormate.presentation.util.CurrentDateTime

class CommunityFragment : BaseFragment<FragmentCommunityBinding>(R.layout.fragment_community) {

    private val commentViewModel: CommentViewModel by viewModels()
    private val boardViewModel: BoardViewModel by viewModels()

    private val commentRecyclerView by lazy { binding.commentRecyclerView }

    private val receiveArgs by lazy { BoardDetail("dd", 0, "dd") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        syncBoardUi()
        syncBoardPostId()
        initBinding()
        viewModelObserve()
    }

    private fun initBinding() {
        binding.commentPost.setOnClickListener {
            if (binding.reply.text.isNotEmpty())
                handleComment()
            else
                requireContext().showToast(NO_POST_TRY_AGAIN)
        }
        binding.edit.setOnClickListener { showPopUpMenu(it) }
        binding.edit.setOnClickListener { showPopUpMenu(it) }
    }

    private fun viewModelObserve() {
        observeBoardViewModel()
        commentViewModelObserve()
    }

    private fun commentViewModelObserve() {
        observeCommentList()
        observePostCommentSuccess()
        observeDeleteCommentSuccess()
        reportCommentSuccessObserve()
    }

    private fun reportCommentSuccessObserve() {
        commentViewModel.reportCommentSuccess.observe(viewLifecycleOwner) { success ->
            if (success)
                requireContext().showToast(REPORT_COMMENT_COMPLETE)
        }
    }

    private fun observeDeleteCommentSuccess() {
        commentViewModel.deleteCommentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                requireContext().showToast(DELETE_COMMENT_COMPLETE)
                getCommentList()
            }
        }
    }

    private fun observeBoardViewModel() {
        observeBoardRemoveSuccess()
        observeBoardReportSuccess()
    }

    private fun observePostCommentSuccess() {
        commentViewModel.postCommentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                getCommentList()
                requireContext().showToast(POST_COMMENT_COMPLETE)
            } else
                requireContext().showToast(COMMENT_UPLOAD_FAIL)
        }
    }

    private fun observeCommentList() {
        commentViewModel.commentList.observe(viewLifecycleOwner) { commentList ->
            syncCommentAdapter(commentList)
        }
    }

    private fun observeBoardReportSuccess() {
        boardViewModel.boardReportSuccess.observe(viewLifecycleOwner) {
            if (it)
                requireContext().showToast(REPORT_POST)
        }
    }

    private fun observeBoardRemoveSuccess() {
        boardViewModel.boardRemoveSuccess.observe(viewLifecycleOwner) {
            if (it) {
                requireContext().showToast(REMOVE_POST_COMPLETE)
            }
        }
    }

    private fun syncBoardPostId() {
        BoardData.boardPostId = receiveArgs.postId
    }

    private fun handleComment() {
        uploadComment()
        binding.reply.text.clear()
        syncCommentPosition()

        /*
        val imm = getSystemService(re.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.reply.windowToken, 0)
        */
    }

    private fun showPopUpMenu(view: View) {
        val popUpMenu = PopupMenu(requireContext(), view)
        selectPopupMenu(popUpMenu)
        clickPopUpItem(popUpMenu)
        popUpMenu.show()
    }

    private fun clickPopUpItem(popupMenu: PopupMenu) {

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_report -> {
                    showReportDialog()
                    true
                }

                R.id.action_delete -> {
                    showDeleteAlertDialog()
                    true
                }

                R.id.action_edit -> {
                    moveCommunityEditFragment()
                    true
                }

                else -> false
            }
        }
    }

    private fun selectPopupMenu(popupMenu: PopupMenu) {
        if (UserKakaoIntel.userId != receiveArgs.userId.toString())
            popupMenu.menuInflater.inflate(R.menu.post_report, popupMenu.menu)
        else
            popupMenu.menuInflater.inflate(R.menu.post_menu, popupMenu.menu)
    }

    private fun moveCommunityEditFragment() {
        /*
        val intent = Intent(this, BoardEditActivity::class.java)
        intent.putExtra(FirebasePathConstant.POST_PATH_INTENT, receiveArgs)
        activityResultLauncher.launch(intent)
         */
    }

    private fun showDeleteAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(REMOVE_POST_ASKING)
            .setPositiveButton(YES) { _, _ -> removeBoard() }
            .setNegativeButton(NO) { _, _ -> }
            .create()
            .show()
    }

    private fun removeBoard() {
        boardViewModel.removePost(receiveArgs.postId)
    }

    private fun syncCommentPosition() {
        commentSize += 1
        commentRecyclerView.scrollToPosition(commentSize - 1)
    }

    private fun getCommentList() {
        commentViewModel.getComment()
    }

    private fun uploadComment() {
        val uploadComment = Comment(
            "",
            UserKakaoIntel.userId.toLong(),
            UserKakaoIntel.userNickName,
            UserKakaoIntel.userProfileImg,
            binding.reply.text.toString(),
            CurrentDateTime.getCommentTime()
        )

        commentViewModel.uploadComment(uploadComment, receiveArgs.postId)
    }

    private fun tossIntent(entirePage: Int, curPage: Int, imgUri: String) {
        /*
        val intent = Intent(this, ImageDetailActivity::class.java)
        intent.putExtra("entirePage", entirePage)
        intent.putExtra("currentPage", curPage)
        intent.putExtra("imgUrl", imgUri)
        startActivity(intent)
         */
    }

    private fun showReportDialog() {

        val reasons = arrayOf("욕설", "도배", "인종 혐오 표현", "성적인 만남 유도")
        val checkedReasons = booleanArrayOf(false, false, false, false, false)
        val builder = AlertDialog.Builder(requireContext())
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

    private fun userReport(selectedReasons: MutableList<String>) {
        val reportContent = Report(
            UserKakaoIntel.userId,
            selectedReasons,
            receiveArgs.userId.toString(),
            receiveArgs.postId
        )
        boardViewModel.reportPost(reportContent)
    }

    private fun syncBoardUi() {
        syncUserInfoUi()
        postUiSync()
        bindCommentProfileImg()
        getCommentList()
    }

    private fun bindCommentProfileImg() {
        Glide.with(requireContext())
            .load(UserKakaoIntel.userProfileImg)
            .circleCrop()
            .into(binding.replyImg)
    }

    private fun syncCommentAdapter(list: List<Comment>) {
        commentList = list
        commentSize = commentList.size
        commentRecyclerView.adapter =
            CommentAdapter(commentList, UserKakaoIntel.userId, commentViewModel)
        commentRecyclerView.scrollToPosition(commentList.size - 1)
    }

    private fun postUiSync() {
        binding.postText.text = receiveArgs.post
        syncImgUi()
    }

    private fun syncImgUi() {
        if (receiveArgs.img.size == 0)
            removeImgView()
        else {
            syncImgData()
            clickImageView()
        }
    }

    private fun syncImgData() {

        val imageViewList =
            listOf(binding.uploadImageView1, binding.uploadImageView2, binding.uploadImageView3)

        for (i in receiveArgs.img.indices) {
            Glide.with(requireContext())
                .load(receiveArgs.img[i])
                .override(1100, 1000)
                .into(imageViewList[i])

            imageViewList[i].visibility = View.VISIBLE
        }//사용 하는 imageView 보여 주기

        for (i in receiveArgs.img.size until imageViewList.size) {
            imageViewList[i].visibility = View.GONE
        }//사용 하지 않는 imageView 제거
    }

    private fun clickImageView() {
        binding.apply {
            uploadImageView1.setOnClickListener {
                tossIntent(receiveArgs.img.size, 1, receiveArgs.img[0])
            }

            uploadImageView2.setOnClickListener {
                tossIntent(receiveArgs.img.size, 2, receiveArgs.img[1])
            }

            uploadImageView3.setOnClickListener {
                tossIntent(receiveArgs.img.size, 3, receiveArgs.img[2])
            }
        }
    }

    private fun removeImgView() {

        val parentView1 = binding.uploadImageView1.parent as ViewGroup
        parentView1.removeView(binding.uploadImageView1)

        val parentView2 = binding.uploadImageView2.parent as ViewGroup
        parentView2.removeView(binding.uploadImageView2)

        val parentView3 = binding.uploadImageView3.parent as ViewGroup
        parentView3.removeView(binding.uploadImageView3)

        /*
        binding.commentLinear.setPadding(
            binding.commentLinear.paddingLeft,
            900, binding.commentLinear.paddingRight,
            binding.commentLinear.paddingBottom
        )
         */
    }

    private fun syncUserInfoUi() {

        Glide.with(requireContext())
            .load(receiveArgs.userImg)
            .circleCrop()
            .into(binding.userImg)

        binding.userName.text = receiveArgs.userName
        binding.dateTime.text = receiveArgs.dateTime

    }

    companion object {
        private const val NO_POST_TRY_AGAIN = "글이 없습니다. 다시 작성해주세요."
        private const val REMOVE_POST_ASKING = "게시물을 삭제 하시겠습니까?"
        private const val REMOVE_POST_COMPLETE = "게시물이 삭제 되었습니다."
        private const val POST_COMMENT_COMPLETE = "댓글이 등록 되었습니다."
        private const val DELETE_COMMENT_COMPLETE = "댓글이 삭제 되었습니다."
        private const val REPORT_COMMENT_COMPLETE = "댓글이 신고 되었습니다."
        private const val CANCEL = "취소"
        private const val CHECK = "확인"
        private const val REPORT_POST = "게시물이 신고 되었습니다."
        private const val COMMENT_UPLOAD_FAIL = "댓글 업로드 실패"

        private const val YES = "예"
        private const val NO = "아니오"

        var commentSize = 0
        var commentList = listOf<Comment>()
    }
}