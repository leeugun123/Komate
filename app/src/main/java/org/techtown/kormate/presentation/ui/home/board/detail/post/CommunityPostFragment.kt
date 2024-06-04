package org.techtown.kormate.presentation.ui.home.board.detail.post

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentCommunityPostBinding
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.domain.model.UserKakaoIntel
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.CustomProgressDialog
import org.techtown.kormate.presentation.constant.FirebasePathConstant
import org.techtown.kormate.presentation.ui.home.board.detail.CommunityViewModel
import org.techtown.kormate.presentation.ui.home.board.detail.gallery.GalleryAdapter
import org.techtown.kormate.presentation.util.CurrentDateTime
import org.techtown.kormate.presentation.util.extension.showToast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CommunityPostFragment :
    BaseFragment<FragmentCommunityPostBinding>(R.layout.fragment_community_post) {

    private val postsRef by lazy { Firebase.database.reference.child(FirebasePathConstant.POSTS_PATH) }
    private val postId by lazy { postsRef.push().key }

    private val communityViewModel: CommunityViewModel by viewModels()
    private var goalImg = mutableListOf<String>()

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingApply()
        boardPostSuccessObserve()
        activityResultLauncherInit()
    }

    private fun activityResultLauncherInit() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    addUriImg(it.data)
                    handleSelectedImages(goalImg, binding)
                }
            }
    }

    private fun bindingApply() {

        binding.apply {

            backBtn.setOnClickListener {
                //TODO("뒤로가기 구현")
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

        if (checkPostEmpty(post))
            return

        val customProgressDialog = CustomProgressDialog(requireContext())
        customProgressDialog.show()


        val storageRef = FirebaseStorage.getInstance().reference
        val imageFileNames = mutableListOf<String>()

        goalImg.forEach { imageUri ->

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
                    requireContext().showToast(e.message.toString())
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

        activityResultLauncher.launch(Intent.createChooser(galleryIntent, "Select images"))
    }

    private fun getCurrentTimestamp() =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
            Date()
        )

    private fun checkPostEmpty(post: String): Boolean {
        return if (post.isEmpty() && goalImg.isEmpty()) {
            requireContext().showToast("내용이 없습니다. 내용을 입력 해주세요")
            true
        } else if (post.isEmpty()) {
            requireContext().showToast("글이 없습니다.")
            true
        } else
            false
    }

    private fun boardPostSuccessObserve() {
        communityViewModel.boardPostSuccess.observe(viewLifecycleOwner) { success ->
            if (success)
                requireContext().showToast("게시글이 등록 되었습니다.")
        }
    }

    private fun uploadPost(post: String, picUri: MutableList<String>, dialog: Dialog) {

        val uploadBoardDetail = BoardDetail(
            postId.toString(),
            UserKakaoIntel.userId.toLong(),
            UserKakaoIntel.userNickName,
            UserKakaoIntel.userProfileImg,
            post,
            picUri,
            CurrentDateTime.getPostTime()
        )

        communityViewModel.uploadPost(uploadBoardDetail)

        dialog.dismiss()
    }

    private fun requestCameraPermission(logic: () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {}
            })
            .setDeniedMessage(PERMISSION_ALLOW_MESSAGE)
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CALENDAR
            )
            .check()
    }

    private fun addUriImg(data: Intent?) {

        data?.clipData?.let { clipData ->

            for (i in 0 until clipData.itemCount) {

                if (goalImg.size == 3) {
                    requireContext().showToast("사진은 최대 3장까지 업로드 가능 합니다.")
                    break
                } //사진 개수 제한

                val getUri = clipData.getItemAt(i).uri
                goalImg.add(getUri.toString())

            }

        }

    }


    private fun handleSelectedImages(
        imageUris: MutableList<String>,
        acBinding: FragmentCommunityPostBinding
    ) {

        connectGalleryAdapter(imageUris, acBinding)
        uploadImgButtonTextSync()

    }// 선택한 이미지들을 처리하는 코드를 작성

    @SuppressLint("SetTextI18n")
    private fun uploadImgButtonTextSync() {
        binding.getImgButton.text = "사진 올리기(" + goalImg.size.toString() + "/3)"
    }

    private fun connectGalleryAdapter(
        imageUris: MutableList<String>,
        acBinding: FragmentCommunityPostBinding
    ) {
        binding.ImgRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.ImgRecyclerView.adapter = GalleryAdapter(imageUris, acBinding)
    }

    companion object {
        private const val POST_UPLOAD_COMPLETE_MESSAGE = "게시글이 등록 되었습니다."
        private const val UPLOAD_DOING_MESSAGE = "업로드 중"
        private const val PERMISSION_ALLOW_MESSAGE = "권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]"
    }
}