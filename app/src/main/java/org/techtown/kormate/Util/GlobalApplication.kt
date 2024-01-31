package org.techtown.kormate.Util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import org.techtown.kormate.BuildConfig


class GlobalApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}