package org.techtown.kormate.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.Model.Report
import org.techtown.kormate.R.drawable.ic_baseline_report_24
import org.techtown.kormate.UI.ViewModel.CommentViewModel
import org.techtown.kormate.databinding.CommentimgBinding


class CommentAdapter(
    private val comments : List<Comment>,
    private val userId : String,
    private val postId : String,
    private val commentViewModel : CommentViewModel
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

            Glide.with(itemView)
                .load(comment.userImg)
                .circleCrop()
                .into(binding.commentUserImg)


            binding.commentUserName.text = comment.userName
            binding.commentTime.text = comment.createdTime
            binding.commentText.text= comment.text

            if (userId == comment.userId.toString())
                setDeleteButton(comment)
            else
                setReportButton(comment)

        }

        private fun setDeleteButton(comment: Comment) {

            binding.Button.setOnClickListener {

                val builder = AlertDialog.Builder(binding.root.context)

                builder.setTitle("댓글을 삭제하시겠습니까?")
                builder.setPositiveButton("예") { dialog, _ ->
                    commentViewModel.deleteComment(comment.id)
                }

                builder.setNegativeButton("아니오") { dialog, _ -> }
                builder.create().show()

            }



        }

        private fun setReportButton(comment: Comment) {
            binding.Button.setImageResource(ic_baseline_report_24)
            binding.Button.setOnClickListener {
                showReportDialog(comment)
            }
        }

        private fun showReportDialog(comment: Comment) {
            val reasons = arrayOf("욕설", "도배", "인종 혐오 표현", "성적인 만남 유도")
            val checkedReasons = booleanArrayOf(false, false, false, false, false)

            val builder = AlertDialog.Builder(binding.root.context)

            builder.setTitle("신고 사유를 선택하세요")
            builder.setMultiChoiceItems(reasons, checkedReasons) { _, which, isChecked ->
                checkedReasons[which] = isChecked
            }

            builder.setPositiveButton("확인") { _, _ ->
                handleReport(comment, reasons, checkedReasons)
            }

            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        private fun handleReport(comment: Comment, reasons: Array<String>, checkedReasons: BooleanArray) {

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
