package org.techtown.kormate.UI.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    private var imageUris = mutableListOf<String>()
    private lateinit var adapter : GalaryAdapter

    private lateinit var receiveList : BoardDetail

    private val postsRef = Firebase.database.reference.child("posts")
    private var commentList = mutableListOf<Comment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        receiveList = intent.getParcelableExtra(POST_PATH_INTENT)!!
        binding.title.text = "게시물 수정"
        binding.updateButton.text = "수정하기"


        receiveList.let {

            binding.post.setText(receiveList.post)

            imageUris = receiveList.img

            if (imageUris.size > 0)
                handleSelectedImages(imageUris , binding)
            //원래 있던 이미지 갤러리 adapter에 띄우기

            commentViewModel.loadComments(receiveList.postId)

            commentViewModel.commentLiveData.observe(this) { commentList ->
                this.commentList = commentList as MutableList<Comment>
            }
            //변하지 않는 데이터

        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.uploadImgButton.setOnClickListener {

            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {

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

            if (post.isEmpty() && imageUris.isEmpty()) {
                Toast.makeText(this, "내용이 없습니다. 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (post.isEmpty()) {
                Toast.makeText(this, "글의 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("수정하는 중")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val imageFileNames = mutableListOf<String>()

            if(imageUris.size == 0){

                upload(post,imageFileNames)
                progressDialog.dismiss()

            }//글만 있는 경우
            else{

                for (i in 0 until imageUris.size) {

                        if (!imageUris[i].startsWith("https")) {

                            val imageFileName = "IMG_${
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                    Date()
                                )
                            }_${UUID.randomUUID()}"

                            val imageRef = storageRef.child("images/$imageFileName")

                            imageRef.putFile(imageUris[i].toUri())
                                .addOnSuccessListener {
                                    imageRef.downloadUrl
                                        .addOnSuccessListener { uri ->

                                            imageFileNames.add(uri.toString())

                                            if (imageFileNames.size == imageUris.size) {

                                                upload(post,imageFileNames)
                                                progressDialog.dismiss()

                                            }


                                        }

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                                }


                        }
                        else {

                                imageFileNames.add(imageUris[i])

                                if (i == imageUris.size - 1) {

                                    upload(post, imageFileNames)
                                    progressDialog.dismiss()

                                }

                        }

                }


            }


        }//업로드 버튼


        boardPostViewModel.postLiveData.observe(this) { success ->

            if (success) {

                restoreComment()
                //댓글 복구
                complete()

            }

        }



    }

    private fun complete() {

        val resIntent = Intent()
        setResult(Activity.RESULT_OK, resIntent)
        finish()

        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()

    }

    private fun restoreComment(){

        commentList.forEach { comment ->
            postsRef.child(receiveList!!.postId!!)
                .child("comments")
                .child(comment.id.toString())
                .setValue(comment)
        }

    }

    private fun upload(post : String, imageFileNames : MutableList<String>){

        receiveList.let {

            val reviseList = BoardDetail(

                receiveList.postId,
                receiveList.userId,
                receiveList.userName,
                receiveList.userImg,
                post,
                imageFileNames,
                receiveList.dateTime

            )

            boardPostViewModel.uploadPost(postsRef, reviseList)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {

            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {

                    if (imageUris.size == 3) {

                        Toast.makeText(this, "사진은 최대 3장까지 업로드 가능합니다.", Toast.LENGTH_SHORT).show()
                        break

                    }

                    val uri = clipData.getItemAt(i).uri

                    imageUris.add(uri.toString())

                }
            }

            handleSelectedImages(imageUris, binding)


        }


    }


    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding) {


        adapter = GalaryAdapter(imageUris,acBinding)
        adapter.notifyDataSetChanged()

        binding.uploadImgButton.text = "사진 올리기(${imageUris.size}/3)"

        binding.ImgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding.ImgRecyclerView.adapter = adapter

    }



}
