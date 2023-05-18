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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.techtown.kormate.CurrentDateTime
import org.techtown.kormate.Fragment.Adapter.GalaryAdapter
import org.techtown.kormate.Fragment.Data.BoardDetail
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import java.text.SimpleDateFormat
import java.util.*

//게시글 수정하기 기능

class BoardEditActivity : AppCompatActivity() {

    private var binding : ActivityBoardPostBinding? = null

    private val REQUEST_CODE_PICK_IMAGES = 1
    private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    private var imageUris = mutableListOf<Uri>()
    private var adapter : GalaryAdapter? = null

    private var picUri: MutableList<String> = mutableListOf()


    private val postsRef = Firebase.database.reference.child("posts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.title.text = "게시물 수정"
        binding!!.updateButton.text = "수정하기"

        val receiveData = intent.getParcelableExtra<BoardDetail>("postIntel")
        var list : BoardDetail? = null

        if(receiveData != null){

            list = receiveData
            //이전 액티비티 Intent 가져오기

            if(list != null){

                binding!!.post.setText(list.post.toString())
                picUri = list!!.img
                //이전 정보 갱신

                if(picUri.size > 0){

                    for (i in 0 until picUri.size) {
                        imageUris.add(picUri[i].toUri())
                    }

                    handleSelectedImages(imageUris, binding!!)


                }//사진이 있는 경우


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

                val imageFileName1 = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_${UUID.randomUUID()}"

                val imageRef1 = storageRef.child("images/$imageFileName1")

                imageRef1.putFile(imageUris[0]!!)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef1.downloadUrl
                            .addOnSuccessListener { uri ->

                                picUri.add(uri.toString())

                                //사진이 2장인 경우,
                                if (imageUris.size > 1){

                                    val imageFileName2 = "IMG_${
                                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                            Date()
                                        )}_${UUID.randomUUID()}"
                                    val imageRef2 = storageRef.child("images/$imageFileName2")

                                    imageRef2.putFile(imageUris[1]!!)
                                        .addOnSuccessListener{ taskSnapshot ->
                                            imageRef2.downloadUrl
                                                .addOnSuccessListener{ uri ->

                                                    picUri.add(uri.toString())

                                                    //사진이 3장인 경우
                                                    if(imageUris.size > 2){

                                                        val imageFileName3 = "IMG_${
                                                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                                                Date()
                                                            )}_${UUID.randomUUID()}"
                                                        val imageRef3 = storageRef.child("images/$imageFileName3")

                                                        imageRef3.putFile(imageUris[2]!!)
                                                            .addOnSuccessListener { taskSnapshot ->
                                                                imageRef3.downloadUrl
                                                                    .addOnSuccessListener { uri ->

                                                                        picUri.add(uri.toString())

                                                                        postsRef.child(list!!.postId!!).setValue(BoardDetail(list.postId, list.userId, list.userName,
                                                                            list.userImg, post, picUri!!, list.dateTime))


                                                                        progressDialog.dismiss()

                                                                        complete()

                                                                    }
                                                            }



                                                    }
                                                    else{

                                                        postsRef.child(list!!.postId!!).setValue(BoardDetail(list.postId, list.userId, list.userName,
                                                            list.userImg, post, picUri!!, list.dateTime))

                                                        progressDialog.dismiss()

                                                        complete()

                                                    }



                                                }

                                        }
                                        .addOnFailureListener { e ->

                                            Toast.makeText(this, "2번째 사진 업로드 실패 " + e.message, Toast.LENGTH_SHORT).show()

                                        }




                                }else{

                                    postsRef.child(list!!.postId!!).setValue(BoardDetail(list.postId, list.userId, list.userName,
                                        list.userImg, post, picUri!!, list.dateTime))

                                    progressDialog.dismiss()

                                    complete()

                                }




                            }
                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

                    }





            }
            //비동기적으로 구현됨
            else {

                val boardPost = BoardDetail(list!!.postId, list.userId, list.userName, list.userImg, post, picUri, list.dateTime)

                postsRef.child(list.postId!!).setValue(boardPost)

                // ProgressDialog 닫기
                progressDialog.dismiss()

                complete()

            }

        }



        binding!!.backBtn.setOnClickListener {
            finish()
        }//뒤로가기



    }

    private fun complete(){

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

                        if(imageUris.size == 3){
                            Toast.makeText(this, "사진은 최대 3장까지 업로드 가능합니다.", Toast.LENGTH_SHORT).show()
                            break
                        }

                        //사진 개수 제한

                        val uri = clipData.getItemAt(i).uri
                        imageUris.add(uri)

                    }
                }

            } else if (data?.data != null) {
                // 단일 이미지를 선택한 경우
                val uri = data.data

                if (imageUris.size < 3 && uri != null) {
                    imageUris.add(uri)
                }

            }

            //정상적으로 사진을 골랐을때
            binding!!.uploadImgButton.setText("사진 올리기(" + imageUris.size.toString() + "/3)")


            handleSelectedImages(imageUris, binding!!)


        }

    }//갤러리로 이동했을때

    private fun handleSelectedImages(imageUris: MutableList<Uri>, acBinding : ActivityBoardPostBinding) {

        adapter = GalaryAdapter(imageUris,acBinding)
        adapter!!.notifyDataSetChanged()

        binding!!.uploadImgButton.setText("사진 올리기(" + imageUris.size.toString() + "/3)")

        Log.e("TAG","갤러리 선택됨")

        binding!!.imgRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding!!.imgRecyclerView.adapter = adapter


    }// 선택한 이미지들을 처리하는 코드를 작성


}