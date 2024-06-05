package org.techtown.kormate.presentation.ui.signup

import androidx.lifecycle.ViewModel
import org.techtown.kormate.data.repository.MyIntelRepository

class SignUpViewModel : ViewModel() {

    private val myIntelRepository = MyIntelRepository()

    var selectNation = ""
    var major = ""
    var selfIntro = ""
    var gender = ""

    fun join() {
        //myIntelRepository.repoUploadUserIntel(UserIntel(selectNation,major,selfIntro,gender))
    }
}