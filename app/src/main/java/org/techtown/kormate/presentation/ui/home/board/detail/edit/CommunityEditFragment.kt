package org.techtown.kormate.presentation.ui.home.board.detail.edit

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentCommunityPostBinding
import org.techtown.kormate.domain.model.BoardDetail
import org.techtown.kormate.presentation.ui.home.board.detail.CommunityFragment
import org.techtown.kormate.presentation.ui.home.board.detail.CommunityViewModel
import org.techtown.kormate.presentation.ui.home.board.detail.comment.CommentViewModel
import org.techtown.kormate.presentation.ui.home.board.detail.gallery.GalleryAdapter
import org.techtown.kormate.presentation.util.BoardData
import org.techtown.kormate.presentation.util.CustomProgressDialog
import org.techtown.kormate.presentation.util.base.BaseFragment
import org.techtown.kormate.presentation.util.extension.showToast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CommunityEditFragment :
    BaseFragment<FragmentCommunityPostBinding>(R.layout.fragment_community_post) {

    private val communityViewModel: CommunityViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()

    private lateinit var receiveIntent: BoardDetail
    private lateinit var goalImg: MutableList<String>

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUiText()
        initBinding()

        getIntentHandling()
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

    private fun initBinding() {
        binding.onBackBtnClick = ::navigateCommunityFragment
        binding.onGetImageBtnClick = ::navigateGalleryActivity
        binding.onPostImageBtnClick = ::processImageAndText
    }

    private fun navigateCommunityFragment() {
        findNavController().popBackStack()
    }

    private fun navigateGalleryActivity() {
        requestCameraPermission { moveToGallery() }
    }

    private fun processImageAndText() {
        val post = binding.post.text.toString()

        if (checkPostEmpty(post))
            return

        val customProgressDialog = CustomProgressDialog(requireContext())
        customProgressDialog.show()

        val storageRef = FirebaseStorage.getInstance().reference
        val imageFileNames = mutableListOf<String>()

        fun uploadImage(uri: Uri) {

            val imageFileName = "IMG_${getCurrentTimestamp()}_${UUID.randomUUID()}"
            val imageRef = storageRef.child("images/$imageFileName")

            imageRef.putFile(uri)
                .addOnSuccessListener { _ ->
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            imageFileNames.add(uri.toString())
                            checkUploadCompletion(
                                imageFileNames.size,
                                goalImg.size,
                                post,
                                imageFileNames,
                                customProgressDialog
                            )
                        }
                }
                .addOnFailureListener { e ->
                    requireContext().showToast(e.message.toString())
                    customProgressDialog.dismiss()
                }

        }

        if (goalImg.size == 0) {
            postUpload(post, imageFileNames)
        }//글만 있는 경우
        else {

            goalImg.forEach { imageUrl ->

                if (!imageUrl.startsWith("https"))
                    uploadImage(imageUrl.toUri()) //upload 되지 않는 사진인 경우 파이어베이스를 거침
                else {
                    imageFileNames.add(imageUrl)
                    checkUploadCompletion(
                        imageFileNames.size,
                        goalImg.size,
                        post,
                        imageFileNames,
                        customProgressDialog
                    )
                }

            }

        }
    }

    private fun boardPostSuccessObserve() {
        communityViewModel.boardPostSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                restoreComment()
                requireContext().showToast(REVISE_POST_COMPLETE_MESSAGE)
            }
        }
    }

    private fun checkPostEmpty(post: String) =
        if (post.isEmpty() && goalImg.isEmpty()) {
            requireContext().showToast("내용이 없습니다. 내용을 입력 해주세요")
            true
        } else if (post.isEmpty()) {
            requireContext().showToast("글이 없습니다.")
            true
        } else
            false

    private fun moveToGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        galleryIntent.let {
            it.type = "image/*"
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            it.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }

        activityResultLauncher.launch(Intent.createChooser(galleryIntent, "Select images"))
    }

    private fun getCurrentTimestamp() =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    private fun checkUploadCompletion(
        currentSize: Int,
        totalSize: Int,
        post: String,
        imageFileNames: MutableList<String>,
        dialog: Dialog
    ) {
        if (currentSize == totalSize) {
            postUpload(post, imageFileNames)
            dialog.dismiss()
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
                handleSelectedImages(boardDetail.img, binding)
            //원래 있던 이미지 갤러리 adapter에 띄우기
        }
    }

    private fun receiveIntentInit() {
        // receiveIntent = intent.getParcelableExtra(FirebasePathConstant.POST_PATH_INTENT)!!
        goalImg = receiveIntent.img
    }

    private fun initUiText() {
        binding.title.text = requireContext().getString(R.string.post_revise)
        binding.updateButton.text = requireContext().getString(R.string.revise)
    }

    private fun restoreComment() {
        CommunityFragment.commentList.forEach { comment ->
            commentViewModel.uploadComment(comment, BoardData.boardPostId)
        }
    }

    private fun postUpload(post: String, imageFileNames: MutableList<String>) {

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
            communityViewModel.uploadPost(reviseList)
        }
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
                }

                val getUri = clipData.getItemAt(i).uri.toString()
                goalImg.add(getUri)

            }
        }
    }


    private fun handleSelectedImages(
        imageUris: MutableList<String>,
        acBinding: FragmentCommunityPostBinding
    ) {
        connectGalleryAdapter(imageUris, acBinding)
        uploadImgTextSync(imageUris)
    }

    private fun connectGalleryAdapter(
        imageUris: MutableList<String>,
        acBinding: FragmentCommunityPostBinding
    ) {
        binding.ImgRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), IMAGE_LAYOUT_COUNT)
        binding.ImgRecyclerView.adapter = GalleryAdapter(imageUris, acBinding)
    }

    private fun uploadImgTextSync(imageUris: MutableList<String>) {
        binding.getImgButton.text = "사진 올리기(${imageUris.size}/3)"
    }

    companion object {
        private const val REVISE_POST_COMPLETE_MESSAGE = "게시글이 수정 되었습니다."
        private const val PERMISSION_ALLOW_MESSAGE = "권한을 허용 해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]"
        private const val IMAGE_LAYOUT_COUNT = 3
    }
}
