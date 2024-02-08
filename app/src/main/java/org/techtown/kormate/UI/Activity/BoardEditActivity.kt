package org.techtown.kormate.UI.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.FirebasePathConstant.COMMENT_PATH
import org.techtown.kormate.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.FirebasePathConstant.POST_PATH_INTENT
import org.techtown.kormate.UI.Adapter.GalaryAdapter
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.UI.ViewModel.BoardPostViewModel
import org.techtown.kormate.UI.ViewModel.CommentViewModel
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBoardPostBinding.inflate(layoutInflater) }
    private val commentViewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }
    private val boardPostViewModel by lazy { ViewModelProvider(this)[BoardPostViewModel::class.java] }
    private val postsRef by lazy { Firebase.database.reference.child(POSTS_PATH)}

    private lateinit var commentList : MutableList<Comment>
    private lateinit var receiveIntent : BoardDetail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        syncTitleUi()
        getIntentHandling()

        commentViewModel.commentLiveData.observe(this) { commentList ->
            this.commentList = commentList as MutableList<Comment>
        }


        binding.backBtn.setOnClickListener { finish() }

        binding.uploadImgButton.setOnClickListener {

            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        moveSelectGallery()
                    }

                    private fun moveSelectGallery() {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                        startActivityForResult(
                            Intent.createChooser(intent, "Select images"),
                            REQUEST_CODE_PICK_IMAGES
                        )
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {}
                })
                .setPermissions(PERMISSION_READ_EXTERNAL_STORAGE)
                .check()
        }


        binding.updateButton.setOnClickListener {

            val post = binding.post.text.toString()

            if (post.isEmpty() && receiveIntent.img.isEmpty()) {
                showToast("내용이 없습니다. 내용을 입력해주세요")
                return@setOnClickListener
            }

            if (post.isEmpty()) {
                showToast("글의 내용이 없습니다.")
                return@setOnClickListener
            }

            val progressBar = createProgressBar()

            val storageRef = FirebaseStorage.getInstance().reference
            val imageFileNames = mutableListOf<String>()

            fun uploadImage(uri : Uri) {

                val imageFileName = "IMG_${getCurrentTimestamp()}_${UUID.randomUUID()}"
                val imageRef = storageRef.child("images/$imageFileName")

                imageRef.putFile(uri)
                    .addOnSuccessListener { _ ->
                        imageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                imageFileNames.add(uri.toString())
                                checkUploadCompletion(imageFileNames.size, receiveIntent.img.size, post, imageFileNames, progressBar)
                            }
                    }
                    .addOnFailureListener { e ->
                        showToast(e.message.toString())
                        progressBar.dismiss()
                    }
            }

            receiveIntent.img.forEach {imageUrl ->

                if (!imageUrl.startsWith("https"))
                    uploadImage(imageUrl.toUri())
                else {
                    imageFileNames.add(imageUrl)
                    checkUploadCompletion(imageFileNames.size, receiveIntent.img.size, post, imageFileNames, progressBar)
                }

            }

        }

        boardPostViewModel.postLiveData.observe(this) { success ->

            if (success) {
                restoreComment()
                reviseComplete()
            }

        }


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createProgressBar(): ProgressDialog {
        val progressBar = ProgressDialog(this)
        progressBar.setMessage("수정하는 중")
        progressBar.setCancelable(false)
        progressBar.show()
        return progressBar
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    private fun checkUploadCompletion(currentSize: Int, totalSize: Int, post: String, imageFileNames: MutableList<String>, progressBar: ProgressDialog) {
        if (currentSize == totalSize) {
            upload(post, imageFileNames)
            progressBar.dismiss()
        }
    }

    private fun getIntentHandling() {
        receiveIntentInit()
        receiveUiBinding()
    }

    private fun receiveUiBinding() {

        receiveIntent.let { boardDetail ->

            binding.post.setText(boardDetail.post)

            if (boardDetail.img.isNotEmpty())
                handleSelectedImages(boardDetail.img , binding)
            //원래 있던 이미지 갤러리 adapter에 띄우기

            commentViewModel.loadComments(boardDetail.postId)

        }
    }

    private fun receiveIntentInit() {
        receiveIntent = intent.getParcelableExtra(POST_PATH_INTENT)!!
    }

    private fun syncTitleUi() {
        binding.title.text = "게시물 수정"
        binding.updateButton.text = "수정하기"
    }

    private fun reviseComplete() {

        val resIntent = Intent()
        setResult(Activity.RESULT_OK, resIntent)
        finish()

        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()

    }

    private fun restoreComment(){

        commentList.forEach { comment ->
            postsRef.child(receiveIntent.postId)
                .child(COMMENT_PATH)
                .child(comment.id)
                .setValue(comment)
        }

    }

    private fun upload(post : String, imageFileNames : MutableList<String>){

        receiveIntent.let {

            val reviseList = BoardDetail(
                receiveIntent.postId,
                receiveIntent.userId,
                receiveIntent.userName,
                receiveIntent.userImg,
                post,
                imageFileNames,
                receiveIntent.dateTime
            )

            boardPostViewModel.uploadPost(postsRef, reviseList)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            addUriImg(data)
            handleSelectedImages(receiveIntent.img, binding)
        }

    }

    private fun addUriImg(data: Intent?) {
        data?.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {

                if (receiveIntent.img.size == 3) {
                    Toast.makeText(this, "사진은 최대 3장까지 업로드 가능합니다.", Toast.LENGTH_SHORT).show()
                    break
                }

                val getUri = clipData.getItemAt(i).uri.toString()
                receiveIntent.img.add(getUri)

            }
        }
    }


    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding) {

        connectGalleryAdapter(imageUris , acBinding)
        binding.uploadImgButton.text = "사진 올리기(${imageUris.size}/3)"

    }

    private fun connectGalleryAdapter(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding ) {

        val adapter = GalaryAdapter(imageUris , acBinding)
        adapter.notifyDataSetChanged()
        binding.ImgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding.ImgRecyclerView.adapter = adapter

    }


    companion object{
        private const val REQUEST_CODE_PICK_IMAGES = 1
        private const val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    }


}
