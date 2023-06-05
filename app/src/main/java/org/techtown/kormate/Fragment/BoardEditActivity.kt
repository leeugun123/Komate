package org.techtown.kormate.Fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
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
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardPostBinding

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    private val imageUris = mutableListOf<String>()
    private var adapter: GalaryAdapter? = null
    private var picUri = mutableListOf<String>()
    private var list: BoardDetail? = null

    private val postsRef = Firebase.database.reference.child("posts")
    private val commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.text = "게시물 수정"
        binding.updateButton.text = "수정하기"

        val receiveData = intent.getParcelableExtra<BoardDetail>("postIntel")


        if (receiveData != null) {
            list = receiveData

            if (list != null) {
                binding.post.setText(list!!.post.toString())
                picUri = list!!.img

                if (picUri.size > 0) {
                    handleSelectedImages(picUri, binding)
                }

                val commentsRef =
                    Firebase.database.reference.child("posts").child(list!!.postId.toString())
                        .child("comments")

                commentsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        commentList.clear()

                        for (snapshot in dataSnapshot.children) {
                            val comment = snapshot.getValue(Comment::class.java)

                            if (comment != null) {
                                commentList.add(comment)
                            }
                        }

                        Log.e("TAG", commentList.size.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG", "댓글 조회 실패")
                    }
                })
            }
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

                                        val boardDetail = BoardDetail(
                                            list!!.postId,
                                            list!!.userId,
                                            list!!.userName,
                                            list!!.userImg,
                                            post,
                                            mergeTwoLists(picUri, imageFileNames),
                                            list!!.dateTime
                                        )

                                        postsRef.child(list!!.postId!!).setValue(boardDetail)
                                        uploadComment()
                                        //댓글 정보 유지하기

                                        progressDialog.dismiss()
                                        complete(boardDetail)
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                val boardDetail = BoardDetail(
                    list!!.postId,
                    list!!.userId,
                    list!!.userName,
                    list!!.userImg,
                    post,
                    picUri,
                    list!!.dateTime
                )

                postsRef.child(list!!.postId!!).setValue(boardDetail)
                uploadComment()
                //댓글 정보 유지하기

                progressDialog.dismiss()
                complete(boardDetail)
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }

    private fun complete(boardDetail: BoardDetail) {

        val resIntent = Intent()
        resIntent.putExtra("resIntent", boardDetail)
        setResult(Activity.RESULT_OK, resIntent)
        finish()
        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()

    }

    private fun uploadComment(){

        for (i in 0 until commentList.size) {
            postsRef.child(list!!.postId!!)
                .child("comments")
                .child(commentList[i].id.toString())
                .setValue(commentList[i])
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        if (picUri.size + imageUris.size == 3) {
                            Toast.makeText(
                                this,
                                "사진은 최대 3장까지 업로드 가능합니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                        val uri = clipData.getItemAt(i).uri
                        imageUris.add(uri.toString())
                    }
                }
            } else if (data?.data != null) {
                val uri = data.data

                if (picUri.size + imageUris.size < 3 && uri != null) {
                    imageUris.add(uri.toString())
                } else {
                    Toast.makeText(
                        this,
                        "사진은 최대 3장까지 업로드 가능합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            handleSelectedImages(mergeTwoLists(picUri, imageUris), binding)
        }
    }


    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding: ActivityBoardPostBinding) {
        adapter = GalaryAdapter(imageUris, acBinding)
        adapter!!.notifyDataSetChanged()

        binding.uploadImgButton.text = "사진 올리기(${imageUris.size}/3)"
        binding.imgRecyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.imgRecyclerView.adapter = adapter
    }


    private fun mergeTwoLists(list1: MutableList<String>, list2: MutableList<String>): MutableList<String> {
        val mergedList = mutableListOf<String>()
        mergedList.addAll(list1)
        mergedList.addAll(list2)
        return mergedList
    }


}
