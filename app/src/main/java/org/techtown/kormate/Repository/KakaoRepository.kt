package org.techtown.kormate.Repository

import android.app.Application
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.UserKakaoIntel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KakaoRepository() {

    suspend fun repoLoadUserData(): Boolean {

        val deferred = CompletableDeferred<Boolean>()

        UserApiClient.instance.me { user, error ->

            if (error != null) {
                errorMasterAccountHandle()
                deferred.complete(false)
            } else {
                user?.let {
                    repoUserKakaoBinding(it)
                    deferred.complete(true)
                }
            }

        }

        return deferred.await()

    }

    private fun repoUserKakaoBinding(user : User) {
        UserKakaoIntel.userNickName = user.kakaoAccount?.profile?.nickname.toString()
        UserKakaoIntel.userProfileImg = user.kakaoAccount?.profile?.profileImageUrl.toString()
        UserKakaoIntel.userId = user.id.toString()
    }

    private fun errorMasterAccountHandle() {
        UserKakaoIntel.userNickName = MASTER_ACCOUNT_NAME
        UserKakaoIntel.userProfileImg = MASTER_ACCOUNT_PROFILE_IMG_URI
        UserKakaoIntel.userId = MASTER_ACCOUNT_USER_ID.toString()
    }


    companion object {
        private const val MASTER_ACCOUNT_NAME = "마스터 계정"
        private const val MASTER_ACCOUNT_PROFILE_IMG_URI =
            "https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"
        private const val MASTER_ACCOUNT_USER_ID: Long = 3333

    }




}