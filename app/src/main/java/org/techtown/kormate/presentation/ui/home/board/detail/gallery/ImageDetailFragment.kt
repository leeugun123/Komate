package org.techtown.kormate.presentation.ui.home.board.detail.gallery

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentImageDetailBinding
import org.techtown.kormate.presentation.BaseFragment

class ImageDetailFragment :
    BaseFragment<FragmentImageDetailBinding>(R.layout.fragment_image_detail) {

    private val receiveUrl by lazy { /*"intent.getStringExtra("imgUrl")"*/ "" }
    private val entirePage by lazy { /* intent.getIntExtra("entirePage", ENTIRE_PAGE_NUM)*/ "" }
    private val curPage by lazy { /* intent.getIntExtra("currentPage", CUR_PAGE_NUM) */ "" }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSync()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bindingApply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindingApply() {
        binding.apply {
            finishButton.setOnClickListener {
                //TODO("끝내기")
            }
            downButton.setOnClickListener {
                imgDownload()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun imgDownload() {

        // 이미지 다운로드 URL
        val request = DownloadManager.Request(Uri.parse(receiveUrl))

        // Set network types, title, and description
        request.apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setTitle("Downloading Image")
            setDescription("Downloading ${receiveUrl.substringAfterLast("/")}")
        }

        // 이미지가 저장될 경로와 파일명을 지정합니다.
        val fileName = "${System.currentTimeMillis()}.jpg"
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filePath = "${directory.absolutePath}/$fileName"
        request.setDestinationUri(Uri.parse("file://$filePath"))

        // DownloadManager를 통해 이미지를 다운로드합니다.
        val downloadManager =
            requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // 이미지 다운로드가 완료되었을 때 브로드캐스트 리시버에서 처리합니다.
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // 다운로드가 완료된 파일의 ID와 현재 다운로드 ID를 비교하여 동일하면 다운로드가 완료된 것입니다.
                if (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId)
                    Toast.makeText(context, IMAGE_DOWNLOAD_COMPLETE, Toast.LENGTH_SHORT).show()
                // 이미지를 열기 위한 인텐트를 생성합니다.
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(
                onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
        }
    }

    private fun uiSync() {
        binding.cur.text = curPage
        binding.entire.text = entirePage

        Glide.with(this)
            .load(receiveUrl)
            .override(IMG_WIDTH_SIZE, IMG_HEIGHT_SIZE)
            .centerCrop()
            .into(binding.detailImg)
    }

    companion object {
        private const val IMG_WIDTH_SIZE = 1500
        private const val IMG_HEIGHT_SIZE = 1500
        private const val IMAGE_DOWNLOAD_COMPLETE = "이미지 다운 완료"
    }
}