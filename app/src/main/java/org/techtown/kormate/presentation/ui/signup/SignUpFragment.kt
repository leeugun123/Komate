package org.techtown.kormate.presentation.ui.signup

import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import androidx.fragment.app.viewModels
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentSplashBinding
import org.techtown.kormate.presentation.util.base.BaseFragment

class SignUpFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_sign_up) {

    private val signUpViewModel : SignUpViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpViewModel
    }
}