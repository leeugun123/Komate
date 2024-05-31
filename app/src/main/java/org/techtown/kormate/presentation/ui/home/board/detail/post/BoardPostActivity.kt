package org.techtown.kormate.presentation.ui.home.board.detail.post

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.presentation.constant.BoardPostConstant.MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE
import org.techtown.kormate.presentation.constant.BoardPostConstant.NO_CONTENT_INPUT_CONTENT_MESSAGE
import org.techtown.kormate.presentation.constant.BoardPostConstant.NO_CONTEXT_MESSAGE
import org.techtown.kormate.presentation.constant.FirebasePathConstant.POSTS_PATH
import org.techtown.kormate.presentation.constant.IntentCode.RESPONSE_CODE_BOARD_SYNC
import org.techtown.kormate.presentation.CustomProgressDialog
import org.techtown.kormate.presentation.util.CurrentDateTime
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.domain.model.UserKakaoIntel.userId
import org.techtown.kormate.domain.model.UserKakaoIntel.userNickName
import org.techtown.kormate.domain.model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import org.techtown.kormate.presentation.ui.home.board.detail.BoardViewModel
import org.techtown.kormate.presentation.ui.home.board.detail.gallery.GalleryAdapter
import java.text.SimpleDateFormat
import java.util.*

class BoardPostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBoardPostBinding.inflate(layoutInflater) }
    private val postsRef by lazy { Firebase.database.reference.child(POSTS_PATH) }
    private val postId by lazy { postsRef.push().key }

    private val boardViewModel : BoardViewModel by viewModels()
    private var goalImg = mutableListOf<String>()

    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bindingApply()
        boardPostSuccessObserve()
        activityResultLauncherInit()

    }

    private fun activityResultLauncherInit() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK){
                    addUriImg(it.data)
                    handleSelectedImages(goalImg, binding)
                }

            }
    }

    private fun bindingApply() {

        binding.apply {

            backBtn.setOnClickListener {
                finish()
            }

            getImgButton.setOnClickListener {
                requestCameraPermission { moveToGallery() }
            }

            updateButton.setOnClickListener {
                imageAndTextProcessing()
            }

        }

    }

    private fun imageAndTextProcessing() {

        val post = binding.post.text.toString()

        if(checkPostEmpty(post))
            return

        val customProgressDialog = CustomProgressDialog(this)
        customProgressDialog.show()


        val storageRef = FirebaseStorage.getInstance().reference
        val imageFileNames = mutableListOf<String>()

        goalImg.forEach{ imageUri ->

            val imageFileName = "IMG_${getCurrentTimestamp()}_${UUID.randomUUID()}"
            val imageRef = storageRef.child("images/$imageFileName")

            imageRef.putFile(imageUri.toUri())
                .addOnSuccessListener { _ ->
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            imageFileNames.add(uri.toString())
                            if (imageFileNames.size == goalImg.size)
                                uploadPost(post, imageFileNames, customProgressDialog)
                        }
                }
                .addOnFailureListener { e ->
                    showToastMessage(e.message.toString())
                    customProgressDialog.dismiss()
                }

        }

        if (goalImg.isEmpty())
            uploadPost(post, mutableListOf(), customProgressDialog)


    }

    private fun moveToGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        galleryIntent.apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }

        activityResultLauncher.launch( Intent.createChooser(galleryIntent, "Select images"))

    }


    private fun getCurrentTimestamp() = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())


    private fun checkPostEmpty(post : String) : Boolean {

        return if (post.isEmpty() && goalImg.isEmpty()) {
            showToastMessage(NO_CONTENT_INPUT_CONTENT_MESSAGE)
            true
        } else if (post.isEmpty()) {
            showToastMessage(NO_CONTEXT_MESSAGE)
            true
        } else
            false

    }

    private fun boardPostSuccessObserve() {

        boardViewModel.boardPostSuccess.observe(this) { success ->
            if (success)
                postComplete()

        }

    }

    private fun uploadPost(post : String ,picUri : MutableList<String> , dialog : Dialog) {

        val uploadBoardDetail = BoardDetail(postId.toString(), userId.toLong(), userNickName, userProfileImg
            , post, picUri, CurrentDateTime.getPostTime())

        boardViewModel.uploadPost(uploadBoardDetail)

        dialog.dismiss()
    }




    private fun postComplete(){
        showToastMessage(POST_UPLOAD_COMPLETE_MESSAGE)
        setResult(RESPONSE_CODE_BOARD_SYNC)
        finish()
    }

    private fun showToastMessage(message : String){
        makeText(this, message, Toast.LENGTH_SHORT).show()
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

    private fun addUriImg(data: Intent?) {

        data?.clipData?.let { clipData ->

            for (i in 0 until clipData.itemCount) {

                if (goalImg.size == 3) {
                    makeText(this, MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE, Toast.LENGTH_SHORT).show()
                    break
                } //사진 개수 제한

                val getUri = clipData.getItemAt(i).uri
                goalImg.add(getUri.toString())

            }

        }

    }


    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding : ActivityBoardPostBinding) {

        connectGalleryAdapter(imageUris , acBinding)
        uploadImgButtonTextSync()

    }// 선택한 이미지들을 처리하는 코드를 작성

    @SuppressLint("SetTextI18n")
    private fun uploadImgButtonTextSync() {
        binding.getImgButton.text = "사진 올리기(" + goalImg.size.toString() + "/3)"
    }

    private fun connectGalleryAdapter(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding ) {

        binding.ImgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding.ImgRecyclerView.adapter = GalleryAdapter(imageUris , acBinding)

    }

    companion object{
        private const val POST_UPLOAD_COMPLETE_MESSAGE = "게시글이 등록 되었습니다."
        private const val UPLOAD_DOING_MESSAGE = "업로드 중"
        private const val PERMISSION_ALLOW_MESSAGE = "권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]"

    }


}