package org.techtown.kormate.Fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.Fragment.Adapter.GalaryAdapter
import org.techtown.kormate.Fragment.Adapter.ReviseGalaryAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.Fragment.ViewModel.BoardPostViewModel
import org.techtown.kormate.Fragment.ViewModel.CommentViewModel
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardPostBinding

    private lateinit var commentViewModel: CommentViewModel
    private lateinit var boardPostViewModel: BoardPostViewModel

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    private val imageUris = mutableListOf<String>()
    private var picUri = mutableListOf<String>()

    private var prior_adapter: ReviseGalaryAdapter? = null
    private var new_adapter : ReviseGalaryAdapter? = null
    //각 리스트와 어뎁터를 따로 분리


    private var receiveList: BoardDetail? = null
    private var reviseList : BoardDetail? = null

    private val postsRef = Firebase.database.reference.child("posts")
    private var commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentViewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
        boardPostViewModel = ViewModelProvider(this).get(BoardPostViewModel::class.java)

        binding.title.text = "게시물 수정"
        binding.updateButton.text = "수정하기"

        val receiveData = intent.getParcelableExtra<BoardDetail>("postIntel")

        if (receiveData != null) {

            receiveList = receiveData

            if (receiveList != null) {

                binding.post.setText(receiveList!!.post.toString())

                picUri = receiveList!!.img

                if (picUri.size > 0) {
                    handleSelectedImages(picUri,imageUris,binding)
                }//원래 있던 이미지 갤러리 adapter에 띄우기

                commentViewModel.loadComments(receiveList!!.postId.toString())

                commentViewModel.commentLiveData.observe(this) { commentList ->
                    this.commentList = commentList as MutableList<Comment>
                }//변하지 않는 데이터

            }

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

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        // 권한이 거부되면 처리합니다.
                        // ...
                    }
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

            if (imageUris.size > 0) {

                val imageFileNames = mutableListOf<String>()

                for (i in 0 until imageUris.size) {
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

                                        picUri.addAll(imageFileNames)

                                        reviseList = BoardDetail(
                                            receiveList!!.postId,
                                            receiveList!!.userId,
                                            receiveList!!.userName,
                                            receiveList!!.userImg,
                                            post,
                                            picUri,
                                            receiveList!!.dateTime
                                        )

                                        boardPostViewModel.uploadPost(postsRef, reviseList!!)

                                        progressDialog.dismiss()

                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                }
            } else {

                reviseList = BoardDetail(
                    receiveList!!.postId,
                    receiveList!!.userId,
                    receiveList!!.userName,
                    receiveList!!.userImg,
                    post,
                    picUri,
                    receiveList!!.dateTime
                )

                boardPostViewModel.uploadPost(postsRef, reviseList!!)

                progressDialog.dismiss()

            }
        }


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

        for (i in 0 until commentList.size) {
            postsRef.child(receiveList!!.postId!!)
                .child("comments")
                .child(commentList[i].id.toString())
                .setValue(commentList[i])
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {

            Log.e("TAG",picUri.size.toString()+"picUrl 이미지")


            if (data?.clipData != null) {

                val clipData = data.clipData

                if (clipData != null) {

                    for (i in 0 until clipData.itemCount) {

                        if (picUri.size + imageUris.size == 3) {

                            Toast.makeText(this, "사진은 최대 3장까지 업로드 가능합니다.", Toast.LENGTH_SHORT).show()
                            break

                        }

                        val uri = clipData.getItemAt(i).uri

                        imageUris.add(uri.toString())


                    }

                }

            }//다중 이미지

            handleSelectedImages(picUri, imageUris, binding)


        }


    }


    private fun handleSelectedImages(picUris : MutableList<String> ,imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding) {

        prior_adapter = ReviseGalaryAdapter(picUris,imageUris,acBinding)
        prior_adapter!!.notifyDataSetChanged()

        new_adapter = ReviseGalaryAdapter(imageUris,picUris,acBinding)
        new_adapter!!.notifyDataSetChanged()

        binding.uploadImgButton.text = "사진 올리기(${ picUris.size + imageUris.size}/3)"


        if(picUris.size == 0)
            binding.priorImgRecyclerView.layoutManager = GridLayoutManager(this,1)
        else
            binding.priorImgRecyclerView.layoutManager = GridLayoutManager(this, picUri.size)


        if(imageUris.size == 0)
            binding.newImgRecyclerView.layoutManager = GridLayoutManager(this,1)
        else
            binding.newImgRecyclerView.layoutManager = GridLayoutManager(this,imageUris.size)


        binding.newImgRecyclerView.adapter = new_adapter
        binding.priorImgRecyclerView.adapter = prior_adapter

    }



}
