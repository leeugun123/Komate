package org.techtown.kormate.Fragment

import android.Manifest
import android.app.Activity
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
import org.techtown.kormate.Fragment.Adapter.GalaryAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.io.File
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


            val post = binding!!.post.text.toString()

            val date : String = getDate()
            val time : String = getTime()


            if(imageUris.size == 1){

                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference

                // 파일 이름 생성
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "IMG_" + timestamp + "_" + UUID.randomUUID().toString()

                // 업로드할 파일 경로 설정
                val imageRef = storageRef.child("images/$imageFileName")

                imageRef.putFile(imageUris.get(0)!!)
                    .addOnSuccessListener {

                        // 업로드 성공 시
                        Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()

                        //업로드는 성공적으로 업로드 됨.

                       imageRef.downloadUrl
                            .addOnSuccessListener { uri ->

                                Log.e("TAG",uri.toString())

                                val boardPost = BoardDetail(userName,userImg,post,uri.toString(),date,time)

                                if (postId != null) {
                                    postsRef.child(postId).setValue(boardPost)
                                }


                            }

                    }
                    .addOnFailureListener { e ->
                        // 업로드 실패 시
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }


            }



            Toast.makeText(this,"게시글이 등록되었습니다.",Toast.LENGTH_SHORT).show()

            finish()

        }//업데이트 버튼


    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDate() : String{

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateString = "$year-${month + 1}-$day"   // 2022-4-22

        return dateString

    }//현재 날짜 가져오기

    @RequiresApi(Build.VERSION_CODES.N)
    fun getTime() : String{

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        var timeString : String? = null

        if(minute.toString().length <= 1)
            timeString = "$hour" +":"+ "0" +"$minute"
        else
            timeString = "$hour:$minute"

        return timeString

    }//현재 시간 가져오기


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