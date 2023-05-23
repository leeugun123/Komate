package org.techtown.kormate.Fragment

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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.CurrentDateTime
import org.techtown.kormate.Fragment.Adapter.CommentAdapter
import org.techtown.kormate.Fragment.Adapter.GalaryAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private var binding : ActivityBoardPostBinding? = null

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    private var imageUris = mutableListOf<String>()

    private var adapter : GalaryAdapter? = null

    private var picUri: MutableList<String> = mutableListOf()

    private val postsRef = Firebase.database.reference.child("posts")

    private var commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.title.text = "게시물 수정"
        binding!!.updateButton.text = "수정하기"
        //xml 수정 변경


        val receiveData = intent.getParcelableExtra<BoardDetail>("postIntel")
        var list : BoardDetail? = null

        if(receiveData != null){

            list = receiveData
            //이전 액티비티 Intent 가져오기

            if(list != null){

                binding!!.post.setText(list.post.toString())
                picUri = list.img
                //이전 정보 갱신

                if(picUri.size > 0){

                    handleSelectedImages(picUri, binding!!)

                }//사진이 있는 경우


                val commentsRef = Firebase.database.reference.child("posts").child(list.postId.toString()).child("comments")

                commentsRef.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        commentList.clear()

                        for(shapshot in dataSnapshot.children){

                            val comment = shapshot.getValue(Comment::class.java)

                            if(comment != null){
                                commentList.add(comment)

                            }

                        }

                        Log.e("TAG",commentList.size.toString())



                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG","댓글 조회 실패")
                    }

                })//댓글 최신화


            }

        }


        binding!!.uploadImgButton.setOnClickListener {


            TedPermission.create()
                .setPermissionListener(object : PermissionListener {

                    override fun onPermissionGranted() {

                        // 권한이 허용되면 갤러리에서 이미지를 선택합니다.
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                        startActivityForResult(Intent.createChooser(intent, "Select images"), REQUEST_CODE_PICK_IMAGES)

                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        // 권한이 거부되면 처리합니다.
                        // ...
                    }
                })
                .setPermissions(PERMISSION_READ_EXTERNAL_STORAGE)
                .check()



        }//사진 올리기


        binding!!.updateButton.setOnClickListener {

            val post = binding!!.post.text.toString()

            if (post.isEmpty() && imageUris.isEmpty()) {
                Toast.makeText(this, "내용이 없습니다. 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }//사진하고 글 둘다 없는 경우

            if (post.isEmpty()) {
                Toast.makeText(this, "글의 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }//글의 내용이 없는 경우

            // ProgressDialog 생성
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("수정하는 중")
            progressDialog.setCancelable(false) // 사용자가 대화 상자를 닫을 수 없도록 설정
            progressDialog.show()

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference


            //사진이 1장인 경우
            if (imageUris.size > 0) {

                val imageFileNames = mutableListOf<String>()

                for (i in 0 until imageUris.size) {
                    val imageFileName = "IMG_${
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                            Date()
                        )}_${UUID.randomUUID()}"
                    val imageRef = storageRef.child("images/$imageFileName")

                    imageRef.putFile(imageUris[i].toUri())
                        .addOnSuccessListener {
                            imageRef.downloadUrl
                                .addOnSuccessListener { uri ->

                                    imageFileNames.add(uri.toString())

                                    if (imageFileNames.size == imageUris.size) {

                                        var boardDetail = BoardDetail(list!!.postId, list.userId, list.userName, list.userImg, post,
                                            mergeTwoLists(picUri, imageFileNames), list.dateTime)

                                        postsRef.child(list!!.postId!!).setValue(boardDetail)

                                        for (i in 0 until commentList.size) {
                                            postsRef.child(list!!.postId!!).child("comments").child(commentList[i].id.toString()).setValue(commentList[i])
                                        }


                                        progressDialog.dismiss()

                                        complete(boardDetail)
                                    }
                                }
                        }
                        .addOnFailureListener { e ->

                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

                        }
                }
            }
            //비동기적으로 구현됨
            else {

                val boardDetail = BoardDetail(list!!.postId, list.userId, list.userName, list.userImg, post, picUri, list.dateTime)

                postsRef.child(list.postId!!).setValue(boardDetail)

                for (i in 0 until commentList.size) {
                    postsRef.child(list!!.postId!!).child("comments").child(commentList[i].id.toString()).setValue(commentList[i])
                }

                // ProgressDialog 닫기
                progressDialog.dismiss()

                complete(boardDetail)



            }

        }


        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로가기


    }

    private fun complete(boardDetail : BoardDetail){

        val resIntent = Intent()
        resIntent.putExtra("resIntent",boardDetail)
        setResult(Activity.RESULT_OK,resIntent)
        //인텐트 응답

        finish()
        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()

    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {

            if (data?.clipData != null) {
                // 다중 이미지를 선택한 경우
                val clipData = data.clipData

                if (clipData != null) {

                    for (i in 0 until clipData.itemCount) {

                        if(picUri.size + imageUris.size == 3){
                            Toast.makeText(this, "사진은 최대 3장까지 업로드 가능합니다.", Toast.LENGTH_SHORT).show()
                            break
                        }
                        //사진 개수 제한

                        val uri = clipData.getItemAt(i).uri
                        imageUris.add(uri.toString())

                    }

                }

            } else if (data?.data != null) {

                // 단일 이미지를 선택한 경우
                val uri = data.data

                if (picUri.size + imageUris.size  < 3 && uri != null) {
                    imageUris.add(uri.toString())
                }
                else
                    Toast.makeText(this, "사진은 최대 3장까지 업로드 가능합니다.", Toast.LENGTH_SHORT).show()

            }

            handleSelectedImages(mergeTwoLists(picUri,imageUris), binding!!)

        }

    }//갤러리로 이동했을때



    private fun handleSelectedImages(imageUris: MutableList<String>, acBinding : ActivityBoardPostBinding) {

        adapter = GalaryAdapter(imageUris,acBinding)
        adapter!!.notifyDataSetChanged()

        binding!!.uploadImgButton.text = "사진 올리기(" + imageUris.size.toString() + "/3)"
        binding!!.imgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding!!.imgRecyclerView.adapter = adapter


    }// 선택한 이미지들을 처리하는 코드를 작성


    private fun mergeTwoLists(list1: MutableList<String>, list2: MutableList<String>) : MutableList<String> {

        val mergedList = mutableListOf<String>()
        mergedList.addAll(list1)
        mergedList.addAll(list2)
        return mergedList

    }//기존 GalaryList와 새로 선택한 GalaryList를 병합



}