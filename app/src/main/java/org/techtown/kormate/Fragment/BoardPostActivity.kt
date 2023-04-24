package org.techtown.kormate.Fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.CurrentDateTime
import org.techtown.kormate.Fragment.Adapter.GalaryAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.Fragment.Data.Comment
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

class BoardPostActivity : AppCompatActivity() {

    private var binding : ActivityBoardPostBinding? = null

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    private var imageUris = mutableListOf<Uri>()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        val postsRef = Firebase.database.reference.child("posts")
        val postId = postsRef.push().key

        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로가기

        var userName : String? = null
        var userImg: String? = null

        UserApiClient.instance.me { user, error ->

            userName = user?.kakaoAccount?.profile?.nickname
            userImg = user?.kakaoAccount?.profile?.profileImageUrl

        }//viewModel를 통해 가져오는 것으로 수정

        binding!!.uploadImgButton.setOnClickListener {

            TedPermission.create()
                .setPermissionListener(object : PermissionListener {

                    override fun onPermissionGranted() {
                        // 권한이 허용되면 갤러리에서 이미지를 선택합니다.
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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


            // ProgressDialog 생성
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("업로드 중")
            progressDialog.setCancelable(false) // 사용자가 대화 상자를 닫을 수 없도록 설정
            progressDialog.show()


            val post = binding!!.post.text.toString()

            if (post.isEmpty() && imageUris.isEmpty()) {
                Toast.makeText(this, "내용이 없습니다. 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            var picUri: String? = null

            if (imageUris.isNotEmpty()) {

                val imageFileName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_${UUID.randomUUID()}"
                val imageRef = storageRef.child("images/$imageFileName")

                imageRef.putFile(imageUris[0]!!)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl
                            .addOnSuccessListener { uri ->

                                picUri = uri.toString()
                                val boardPost = BoardDetail(postId, userName, userImg, post, picUri!!, CurrentDateTime.getPostTime(), mutableListOf())
                                postsRef.child(postId!!).setValue(boardPost)

                                //업로드 화면 구현

                                // ProgressDialog 닫기
                                progressDialog.dismiss()

                                complete()

                            }
                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

                    }
            }
            //비동기적으로 구현됨
            else {

                val boardPost = BoardDetail(postId, userName, userImg, post, picUri, CurrentDateTime.getPostTime(), mutableListOf())
                postsRef.child(postId!!).setValue(boardPost)

                // ProgressDialog 닫기
                progressDialog.dismiss()

                complete()

            }




        }//작성 완료 버튼 클릭 시



    }


    private fun complete(){

        finish()
        Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()

    }




    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {

            Log.e("TAG","응답됨")

            if (data?.clipData != null) {
                // 다중 이미지를 선택한 경우
                val clipData = data.clipData

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        imageUris.add(uri)
                    }
                }

            } else if (data?.data != null) {
                // 단일 이미지를 선택한 경우
                val uri = data.data

                if (uri != null) {
                    imageUris.add(uri)
                }

            }

            handleSelectedImages(imageUris)


        }

    }//갤러리로 이동했을때

    private fun handleSelectedImages(imageUris: List<Uri>) {

        val adapter = GalaryAdapter(imageUris)

        Log.e("TAG","갤러리 선택됨")

        binding!!.imgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding!!.imgRecyclerView.adapter = adapter


    }// 선택한 이미지들을 처리하는 코드를 작성




}