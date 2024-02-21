package org.techtown.kormate.UI.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.Constant.BoardPostConstant.MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE
import org.techtown.kormate.Constant.BoardPostConstant.NO_CONTENT_INPUT_CONTENT_MESSAGE
import org.techtown.kormate.Constant.BoardPostConstant.NO_CONTEXT_MESSAGE
import org.techtown.kormate.Constant.CarmeraPermissionConstant.REQUEST_CODE_PICK_IMAGES
import org.techtown.kormate.Constant.FirebasePathConstant.COMMENT_PATH
import org.techtown.kormate.Constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.Constant.FirebasePathConstant.POST_PATH_INTENT
import org.techtown.kormate.UI.Adapter.GalleryAdapter
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.Comment
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.UI.ViewModel.CommentViewModel
import org.techtown.kormate.Util.BoardData
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*


class BoardEditActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBoardPostBinding.inflate(layoutInflater) }
    private val boardViewModel by lazy { ViewModelProvider(this)[BoardViewModel::class.java] }
    private val commentViewModel by lazy {ViewModelProvider(this)[CommentViewModel::class.java]}
    private lateinit var receiveIntent : BoardDetail
    private lateinit var goalImg : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        syncTitleUi()
        getIntentHandling()

        binding.backBtn.setOnClickListener { finish() }

        binding.getImgButton.setOnClickListener {
            requestCameraPermission { moveToGallery() }
        }


        binding.updateButton.setOnClickListener {

            val post = binding.post.text.toString()

            if (post.isEmpty() && goalImg.isEmpty()) {
                showToast(NO_CONTENT_INPUT_CONTENT_MESSAGE)
                return@setOnClickListener
            }

            if (post.isEmpty()) {
                showToast(NO_CONTEXT_MESSAGE)
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
                                checkUploadCompletion(imageFileNames.size, goalImg.size, post, imageFileNames, progressBar)
                            }
                    }
                    .addOnFailureListener { e ->
                        showToast(e.message.toString())
                        progressBar.dismiss()
                    }

            }


            if(goalImg.size == 0){
                upload(post, imageFileNames) //글만 있는 경우
            }else{
                goalImg.forEach {imageUrl ->

                    if (!imageUrl.startsWith("https"))
                        uploadImage(imageUrl.toUri()) //upload 되지 않는 사진인 경우 파이어베이스를 거침
                    else {
                        imageFileNames.add(imageUrl)
                        checkUploadCompletion(imageFileNames.size, goalImg.size, post, imageFileNames, progressBar)
                    }

                }

            }



        }

        boardViewModel.boardPostSuccess.observe(this) { success ->

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
        progressBar.setMessage(REVISE_DOING_MESSAGE)
        progressBar.setCancelable(false)
        progressBar.show()
        return progressBar
    }

    private fun moveToGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        galleryIntent.let {
            it.type = "image/*"
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            it.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }

        startActivityForResult(
            Intent.createChooser(galleryIntent, "Select images"),
            REQUEST_CODE_PICK_IMAGES
        )

    }



    private fun getCurrentTimestamp() = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    private fun checkUploadCompletion(currentSize: Int, totalSize: Int, post: String, imageFileNames: MutableList<String>, progressBar: ProgressDialog) {

        Log.e("TAG", "$currentSize   $totalSize")

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
        }

    }

    private fun receiveIntentInit() {
        receiveIntent = intent.getParcelableExtra(POST_PATH_INTENT)!!
        goalImg = receiveIntent.img
    }

    private fun syncTitleUi() {
        binding.title.text = "게시물 수정"
        binding.updateButton.text = "수정 하기"
    }

    private fun reviseComplete() {

        setResult(Activity.RESULT_OK,  Intent())
        finish()
        showToast(REVISE_POST_COMPLETE_MESSAGE)

    }

    private fun restoreComment(){

        BoardActivity.commentList.forEach { comment ->
            commentViewModel.uploadComment(comment , BoardData.boardPostId)
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

            boardViewModel.uploadPost(reviseList)

        }

    }

    private fun requestCameraPermission(logic : () -> Unit){

        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }
                override fun onPermissionDenied(deniedPermissions: List<String>) {}

            })
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CALENDAR )
            .check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            addUriImg(data)
            handleSelectedImages(goalImg, binding)
        }

    }

    private fun addUriImg(data: Intent?) {

        data?.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {

                if (goalImg.size == 3) {
                    Toast.makeText(this, MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE, Toast.LENGTH_SHORT).show()
                    break
                }

                val getUri = clipData.getItemAt(i).uri.toString()
                goalImg.add(getUri)

            }
        }
    }


    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding) {

        connectGalleryAdapter(imageUris , acBinding)
        uploadImgTextSync(imageUris)

    }

    private fun connectGalleryAdapter(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding ) {

        binding.ImgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding.ImgRecyclerView.adapter = GalleryAdapter(imageUris , acBinding)

    }

    private fun uploadImgTextSync(imageUris: MutableList<String>,) {
        binding.getImgButton.text = "사진 올리기(${imageUris.size}/3)"
    }


    companion object{
        private const val REVISE_POST_COMPLETE_MESSAGE = "게시글이 수정 되었습니다."
        private const val REVISE_DOING_MESSAGE = "업로드 중"
    }


}
