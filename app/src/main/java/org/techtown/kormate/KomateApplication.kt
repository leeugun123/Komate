package org.techtown.kormate

import android.app.Application
import com.kakao.sdk.common.KakaoSdk


class KomateApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}