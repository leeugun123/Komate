package org.techtown.kormate.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.GenericTransitionOptions.with
import com.bumptech.glide.Glide.with
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.with
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.with
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.kakao.sdk.user.UserApiClient
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.databinding.ActivityBoardPostBinding

class BoardPostActivity : AppCompatActivity() {

    private var binding : ActivityBoardPostBinding? = null

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postsRef = Firebase.database.reference.child("posts")
        val postId = postsRef.push().key

        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)



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

            val boardPost = BoardDetail(userName,userImg,post,"img",date,time)


            postId?.let{
                postsRef.child(it).setValue(boardPost)
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

        /*
            val imageUri = data?.data
            if (imageUri != null) {
                // 이미지를 Firebase Storage에 업로드합니다.
                val imageRef = storageRef.child("my-image.jpg")
                imageRef.putFile(imageUri)
                    .addOnSuccessListener {
                        // 업로드가 성공했을 때 호출됩니다.
                        Log.d(TAG, "Image uploaded successfully")
                    }
                    .addOnFailureListener {
                        // 업로드가 실패했을 때 호출됩니다.
                        Log.e(TAG, "Image upload failed", it)
                    }
            }

        */


        }

    }//갤러리로 이동했을때




}