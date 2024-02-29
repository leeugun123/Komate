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
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.Constant.BoardPostConstant.MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE
import org.techtown.kormate.Constant.BoardPostConstant.NO_CONTENT_INPUT_CONTENT_MESSAGE
import org.techtown.kormate.Constant.BoardPostConstant.NO_CONTEXT_MESSAGE
import org.techtown.kormate.Constant.CarmeraPermissionConstant.REQUEST_CODE_PICK_IMAGES
import org.techtown.kormate.Constant.FirebasePathConstant.POST_PATH_INTENT
import org.techtown.kormate.Constant.IntentCode.RESPONSE_CODE_BOARD_SYNC
import org.techtown.kormate.UI.Adapter.GalleryAdapter
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.UI.ViewModel.BoardViewModel
import org.techtown.kormate.UI.ViewModel.CommentViewModel
import org.techtown.kormate.Util.BoardData
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*


class BoardEditActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBoardPostBinding.inflate(layoutInflater) }

    private val boardViewModel : BoardViewModel by viewModels()
    private val commentViewModel : CommentViewModel by viewModels()

    private lateinit var receiveIntent : BoardDetail
    private lateinit var goalImg : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        syncTitleUi()
        getIntentHandling()
        bindingApply()
        boardPostSuccessObserve()


    }

    private fun bindingApply() {

        binding.apply {

            backBtn.setOnClickListener { finish() }

            getImgButton.setOnClickListener { requestCameraPermission { moveToGallery() } }

            updateButton.setOnClickListener { imageAndTextProcessing() }
        }

    }

    private fun imageAndTextProcessing() {
        val post = binding.post.text.toString()

        if(checkPostEmpty(post))
            return

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
            postUpload(post, imageFileNames)
        }//글만 있는 경우
        else{

            goalImg.forEach { imageUrl ->

                if (!imageUrl.startsWith("https"))
                    uploadImage(imageUrl.toUri()) //upload 되지 않는 사진인 경우 파이어베이스를 거침
                else {
                    imageFileNames.add(imageUrl)
                    checkUploadCompletion(imageFileNames.size, goalImg.size, post, imageFileNames, progressBar)
                }

            }

        }
    }

    private fun boardPostSuccessObserve() {

        boardViewModel.boardPostSuccess.observe(this) { success ->

            if (success) {
                restoreComment()
                reviseComplete()
            }

        }
    }

    private fun checkPostEmpty(post : String) : Boolean {

        return if (post.isEmpty() && goalImg.isEmpty()) {
            showToast(NO_CONTENT_INPUT_CONTENT_MESSAGE)
            true
        } else if (post.isEmpty()) {
            showToast(NO_CONTEXT_MESSAGE)
            true
        } else
            false

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createProgressBar(): ProgressDialog {
        return ProgressDialog(this).apply {
            setMessage(REVISE_DOING_MESSAGE)
            setCancelable(false)
            show()
        }
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

        if (currentSize == totalSize) {
            postUpload(post, imageFileNames)
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
        binding.title.text = BOARD_REVISE_TEXT
        binding.updateButton.text = REVISE_BUTTON_TEXT
    }

    private fun reviseComplete() {

        setResult(RESPONSE_CODE_BOARD_SYNC,  Intent())
        finish()
        showToast(REVISE_POST_COMPLETE_MESSAGE)

    }

    private fun restoreComment(){

        BoardActivity.commentList.forEach { comment ->
            commentViewModel.uploadComment(comment , BoardData.boardPostId)
        }

    }

    private fun postUpload(post : String, imageFileNames : MutableList<String>){

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
            .setDeniedMessage(PERMISSION_ALLOW_MESSAGE)
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

        binding.ImgRecyclerView.layoutManager = GridLayoutManager(this,IMAGE_LAYOUT_COUNT)
        binding.ImgRecyclerView.adapter = GalleryAdapter(imageUris , acBinding)

    }

    private fun uploadImgTextSync(imageUris: MutableList<String>,) {
        binding.getImgButton.text = "사진 올리기(${imageUris.size}/3)"
    }


    companion object{
        private const val REVISE_POST_COMPLETE_MESSAGE = "게시글이 수정 되었습니다."
        private const val REVISE_DOING_MESSAGE = "업로드 중"
        private const val PERMISSION_ALLOW_MESSAGE = "권한을 허용 해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]"
        private const val BOARD_REVISE_TEXT = "게시물 수정"
        private const val REVISE_BUTTON_TEXT = "수정 하기"

        private const val IMAGE_LAYOUT_COUNT = 3
    }


}
