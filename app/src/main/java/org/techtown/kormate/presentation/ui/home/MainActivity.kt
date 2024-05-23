package org.techtown.kormate.presentation.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.mainScreen) as NavHostFragment
    }

    private val navController: NavController by lazy { navHostFragment.navController }

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBackPressedDispatcher()
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback {
            handleBackPressed()
        }
    }

    private fun handleBackPressed() {
        val destination = navController.currentDestination ?: return
        if (destination.id == R.id.HomeFragment) finishSoftly() else navController.navigateUp()
    }

    private fun finishSoftly() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < ASK_AGAIN_EXIT_DURATION) {
            finish()
        } else {
            backPressedTime = currentTime
            Toast.makeText(this, R.string.back_press_exit_guide, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ASK_AGAIN_EXIT_DURATION = 2_000L
    }
}