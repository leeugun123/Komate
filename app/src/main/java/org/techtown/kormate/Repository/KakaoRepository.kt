package org.techtown.kormate.Repository

import android.app.Application
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.techtown.kormate.Model.UserKakaoIntel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KakaoRepository(application : Application) {

    private var repoUserKakaoIntel = UserKakaoIntel

    suspend fun repositoryLoadUserData(): UserKakaoIntel = withContext(Dispatchers.IO) {

        suspendCoroutine { continuation ->

            UserApiClient.instance.me { user, error ->

                if (error != null) {
                    errorMasterAccountHandle()
                    continuation.resumeWithException(error)
                } else {
                    user?.let { repoUserKakaoBinding(it) }
                    continuation.resume(Unit)
                }

            }

        }

        return@withContext repoUserKakaoIntel
    }

    private fun repoUserKakaoBinding(user : User) {
        repoUserKakaoIntel.userNickName = user.kakaoAccount?.profile?.nickname.toString()
        repoUserKakaoIntel.userProfileImg = user.kakaoAccount?.profile?.profileImageUrl.toString()
        repoUserKakaoIntel.userId = user.id.toString()
    }

    private fun errorMasterAccountHandle() {
        repoUserKakaoIntel.userNickName = MASTER_ACCOUNT_NAME
        repoUserKakaoIntel.userProfileImg = MASTER_ACCOUNT_PROFILE_IMG_URI
        repoUserKakaoIntel.userId = MASTER_ACCOUNT_USER_ID.toString()
    }


    companion object {
        private const val MASTER_ACCOUNT_NAME = "마스터 계정"
        private const val MASTER_ACCOUNT_PROFILE_IMG_URI =
            "https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"
        private const val MASTER_ACCOUNT_USER_ID: Long = 3333

    }




}