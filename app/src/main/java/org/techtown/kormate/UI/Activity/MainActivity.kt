package org.techtown.kormate.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.techtown.kormate.UI.Fragment.BoardFragment
import org.techtown.kormate.UI.Fragment.HomeFragment
import org.techtown.kormate.UI.Fragment.MyFragment
import org.techtown.kormate.R
import org.techtown.kormate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val fa by lazy { HomeFragment() }
    private val fb by lazy { BoardFragment() }
    private val fc by lazy { MyFragment() }
    private val fragmentManager by lazy { supportFragmentManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        addFragment()
        
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->

            when(menuItem.itemId){
                R.id.page_home -> { showFa() }
                R.id.page_board -> { showFb() }
                R.id.page_my -> { showFc() }
                else -> throw IllegalArgumentException("유효하지 않습니다.")
            }

            true

        }


    }

    private fun showFc() {
        fragmentManager.beginTransaction().hide(fa).commit()
        fragmentManager.beginTransaction().hide(fb).commit()
        fragmentManager.beginTransaction().show(fc).commit()
    }

    private fun showFb() {
        fragmentManager.beginTransaction().hide(fa).commit()
        fragmentManager.beginTransaction().show(fb).commit()
        fragmentManager.beginTransaction().hide(fc).commit()
    }

    private fun showFa() {
        fragmentManager.beginTransaction().show(fa).commit()
        fragmentManager.beginTransaction().hide(fb).commit()
        fragmentManager.beginTransaction().hide(fc).commit()
    }

    private fun addFragment() {
        fragmentManager.beginTransaction().add(R.id.main_frame, fa).commit()
        fragmentManager.beginTransaction().add(R.id.main_frame, fb).commit()
        fragmentManager.beginTransaction().add(R.id.main_frame, fc).commit()
    }




}