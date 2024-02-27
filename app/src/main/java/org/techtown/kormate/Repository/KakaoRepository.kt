package org.techtown.kormate.Repository

import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.CompletableDeferred
import org.techtown.kormate.Model.UserKakaoIntel
import org.techtown.kormate.UI.Fragment.MyFragment

class KakaoRepository() {

    suspend fun repoLoadUserData(): Boolean {

        val loadDeferred = CompletableDeferred<Boolean>()

        UserApiClient.instance.me { user, error ->

            if (error != null) {
                errorMasterAccountHandle()
                loadDeferred.complete(false)
            } else {
                user?.let {
                    repoUserKakaoBinding(it)
                    loadDeferred.complete(true)
                }
            }

        }

        return loadDeferred.await()

    }

    suspend fun repoKakaoLogout() : Boolean {

        val logOutDeferred = CompletableDeferred<Boolean>()

        UserApiClient.instance.logout { error ->

            if (error != null)
                logOutDeferred.complete(false)
            else
                logOutDeferred.complete(true)

        }

        return logOutDeferred.await()

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