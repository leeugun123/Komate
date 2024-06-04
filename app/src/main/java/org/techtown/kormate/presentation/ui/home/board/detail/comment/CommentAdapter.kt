package org.techtown.kormate.presentation.ui.home.board.detail.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.R.drawable.ic_baseline_report_24
import org.techtown.kormate.databinding.CommentimgBinding
import org.techtown.kormate.domain.model.Comment
import org.techtown.kormate.domain.model.Report


class CommentAdapter(
    private val comments: List<Comment>,
    private val userId: String,
    private val commentViewModel: CommentViewModel
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommentimgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount() = comments.size

    inner class ViewHolder(private val binding: CommentimgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {

            commentTitleUiBinding(comment)
            if (userId == comment.userId.toString())
                setDeleteButton(comment)
            else
                setReportButton(comment)

        }

        private fun commentTitleUiBinding(comment: Comment) {

            Glide.with(itemView)
                .load(comment.userImg)
                .circleCrop()
                .into(binding.commentUserImg)

            binding.apply {
                commentUserName.text = comment.userName
                commentTime.text = comment.createdTime
                commentText.text = comment.text
            }

        }

        private fun setDeleteButton(comment: Comment) {

            binding.Button.setOnClickListener {

                AlertDialog.Builder(binding.root.context)
                    .setTitle("댓글을 삭제하시겠습니까?")
                    .setPositiveButton("예") { _, _ -> commentViewModel.deleteComment(comment.id) }
                    .setNegativeButton("아니오") { dialog, _ -> }
                    .create().show()

            }

        }

        private fun setReportButton(comment: Comment) {

            binding.Button.apply {
                setImageResource(ic_baseline_report_24)
                setOnClickListener { showReportDialog(comment) }
            }

        }

        private fun showReportDialog(comment: Comment) {

            val reasons = arrayOf("욕설", "도배", "인종 혐오 표현", "성적인 만남 유도")
            val checkedReasons = booleanArrayOf(false, false, false, false, false)

            AlertDialog.Builder(binding.root.context)
                .setTitle("신고 사유를 선택하세요")
                .setMultiChoiceItems(reasons, checkedReasons) { _, which, isChecked ->
                    checkedReasons[which] = isChecked
                }
                .setPositiveButton("확인") { _, _ ->
                    handleReport(comment, reasons, checkedReasons)
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()


        }

        private fun handleReport(
            comment: Comment,
            reasons: Array<String>,
            checkedReasons: BooleanArray
        ) {

            val selectedReasons = mutableListOf<String>()

            for (i in reasons.indices) {
                if (checkedReasons[i]) {
                    selectedReasons.add(reasons[i])
                }
            }

            val report = Report(userId, selectedReasons, comment.userId.toString(), comment.id)
            commentViewModel.reportComment(report)

        }

    }


}
