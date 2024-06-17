package org.techtown.kormate.presentation.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentSplashBinding
import org.techtown.kormate.presentation.util.base.BaseFragment


class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateToHomeLazily()
    }

    private fun navigateToHomeLazily() {
        lifecycleScope.launch {
            delay(SPLASH_DURATION)
            moveToHomeFragment()
        }
    }

    private fun moveToHomeFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    companion object {
        private const val SPLASH_DURATION = 1500L // 1.5초
    }
}