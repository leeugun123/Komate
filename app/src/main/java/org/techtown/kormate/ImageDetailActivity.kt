package org.techtown.kormate

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import org.techtown.kormate.databinding.ActivityImageDetailBinding

class ImageDetailActivity : AppCompatActivity() {

    private var binding : ActivityImageDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val receiveUrl = intent.getStringExtra("imgUrl")
        //imgUrl 받아오기

        val entirePage = intent.getIntExtra("entirePage",3)
        val curPage = intent.getIntExtra("currentPage",1)
        //현재 페이지 / 전체페이지 표시

        binding!!.cur.setText(curPage.toString())
        binding!!.entire.setText(entirePage.toString())
        //페이지 text에 표시

        Glide.with(this)
            .load(receiveUrl)
            .override(1500,1500)
            .centerCrop()
            .into(binding!!.detailImg)

        binding!!.finishButton.setOnClickListener {
            finish()
        }

        binding!!.downButton.setOnClickListener {

         // 이미지 다운로드 URL
            val request = DownloadManager.Request(Uri.parse(receiveUrl))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Downloading Image")
            request.setDescription("Downloading ${receiveUrl!!.substring(receiveUrl!!.lastIndexOf("/") + 1)}")

            // 이미지가 저장될 경로와 파일명을 지정합니다.
            val fileName = "${System.currentTimeMillis()}.jpg"
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val filePath = "${directory.absolutePath}/$fileName"
            request.setDestinationUri(Uri.parse("file://$filePath"))

            // DownloadManager를 통해 이미지를 다운로드합니다.
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            // 이미지 다운로드가 완료되었을 때 브로드캐스트 리시버에서 처리합니다.
            val onComplete = object : BroadcastReceiver() {

                override fun onReceive(context: Context?, intent: Intent?) {

                    // 다운로드가 완료된 파일의 ID와 현재 다운로드 ID를 비교하여 동일하면 다운로드가 완료된 것입니다.
                    if (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId) {

                        Toast.makeText(context, "이미지 다운", Toast.LENGTH_SHORT).show()
                        // 이미지를 열기 위한 인텐트를 생성합니다.
                        val openImageIntent = Intent(Intent.ACTION_VIEW)
                        openImageIntent.setDataAndType(Uri.parse("file://$filePath"), "image/*")
                        startActivity(openImageIntent)

                    }
                }
            }

            // 브로드캐스트 리시버를 등록합니다.
            registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        }//다운로드 버튼


    }


}