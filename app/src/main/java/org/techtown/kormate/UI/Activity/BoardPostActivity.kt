package org.techtown.kormate.UI.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.kormate.Constant.BoardPostConstant.MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE
import org.techtown.kormate.Constant.BoardPostConstant.NO_CONTENT_INPUT_CONTENT_MESSAGE
import org.techtown.kormate.Constant.BoardPostConstant.NO_CONTEXT_MESSAGE
import org.techtown.kormate.Constant.CarmeraPermissionConstant
import org.techtown.kormate.Constant.CarmeraPermissionConstant.REQUEST_CODE_PICK_IMAGES
import org.techtown.kormate.Util.CurrentDateTime
import org.techtown.kormate.UI.Adapter.GalleryAdapter
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.Model.UserKakaoIntel.userId
import org.techtown.kormate.Model.UserKakaoIntel.userNickName
import org.techtown.kormate.Model.UserKakaoIntel.userProfileImg
import org.techtown.kormate.UI.ViewModel.BoardPostViewModel
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

class BoardPostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBoardPostBinding.inflate(layoutInflater) }
    private val boardPostViewModel by lazy { ViewModelProvider(this)[BoardPostViewModel::class.java] }
    private val postsRef by lazy { Firebase.database.reference.child("posts") }
    private val postId by lazy { postsRef.push().key }

    private var imageUris = mutableListOf<String>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.getImgButton.setOnClickListener {
            setImgPermission()
        }


        binding.updateButton.setOnClickListener {

            val post = binding.post.text.toString()

            if (post.isEmpty() && imageUris.isEmpty()) {
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

            // Upload images if there are any
            for (imageUri in imageUris) {
                val imageFileName = "IMG_${getCurrentTimestamp()}_${UUID.randomUUID()}"
                val imageRef = storageRef.child("images/$imageFileName")

                imageRef.putFile(imageUri.toUri())
                    .addOnSuccessListener { _ ->
                        imageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                imageFileNames.add(uri.toString())
                                if (imageFileNames.size == imageUris.size)
                                    uploadPost(post, imageFileNames, progressBar)
                            }
                    }
                    .addOnFailureListener { e ->
                        showToast(e.message.toString())
                        progressBar.dismiss()
                    }
            }

            // If there are no images, upload only the post
            if (imageUris.isEmpty())
                uploadPost(post, mutableListOf(), progressBar)

        }

        checkUploadPost()

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }


    private fun checkUploadPost() {

        boardPostViewModel.postLiveData.observe(this) { success ->
            if (success)
                postComplete()
        }

    }

    private fun uploadPost(post : String ,picUri : MutableList<String> , progressDialog : ProgressDialog) {

        val uploadBoardDetail = BoardDetail(postId.toString(), userId.toLong(), userNickName, userProfileImg
            , post, picUri, CurrentDateTime.getPostTime() )

        lifecycleScope.launch(Dispatchers.Main){
            boardPostViewModel.uploadPost(uploadBoardDetail)
        }

        progressDialog.dismiss()
    }

    private fun createProgressBar(): ProgressDialog {

        val progressBar = ProgressDialog(this)
        progressBar.let {
            it.setMessage(UPLOAD_DOING_MESSAGE)
            it.setCancelable(false)
            it.show()
        }
        return progressBar
    }


    private fun postComplete(){
        finish()
        Toast.makeText(this, POST_UPLOAD_COMPLETE_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    private fun setImgPermission() {

        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    moveSelectGallery()
                }

                private fun moveSelectGallery() {

                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    intent.let {
                        it.type = "image/*"
                        it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        it.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    }

                    startActivityForResult(
                        Intent.createChooser(intent, "Select images"),
                        REQUEST_CODE_PICK_IMAGES
                    )
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {}
            })
            .setPermissions(CarmeraPermissionConstant.PERMISSION_READ_EXTERNAL_STORAGE)
            .check()
    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            addUriImg(data)
            handleSelectedImages(imageUris, binding)
        }

    }

    private fun addUriImg(data: Intent?) {

        data?.clipData?.let { clipData ->

            for (i in 0 until clipData.itemCount) {

                if (imageUris.size == 3) {
                    Toast.makeText(this, MAXIMUM_PIC_THREE_POSSIBLE_MESSAGE, Toast.LENGTH_SHORT).show()
                    break
                } //사진 개수 제한

                val getUri = clipData.getItemAt(i).uri
                imageUris.add(getUri.toString())

            }

        }

    }


    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding : ActivityBoardPostBinding) {

        connectGalleryAdapter(imageUris , acBinding)
        uploadImgButtonTextSync()

    }// 선택한 이미지들을 처리하는 코드를 작성

    @SuppressLint("SetTextI18n")
    private fun uploadImgButtonTextSync() {
        binding.getImgButton.text = "사진 올리기(" + imageUris.size.toString() + "/3)"
    }

    private fun connectGalleryAdapter(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding ) {

        binding.ImgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding.ImgRecyclerView.adapter = GalleryAdapter(imageUris , acBinding)

    }

    companion object{
        private const val POST_UPLOAD_COMPLETE_MESSAGE = "게시글이 등록 되었습니다."
        private const val UPLOAD_DOING_MESSAGE = "업로드 중"

    }


}