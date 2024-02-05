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

class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bottomNaviInit()
    }

    private fun bottomNaviInit() {
        supportFragmentManager.beginTransaction().add(R.id.linearLayout, HomeFragment()).commit()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.page_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, HomeFragment()).commitAllowingStateLoss()
                return true
            }
            R.id.page_board -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, BoardFragment()).commitAllowingStateLoss()
                return true
            }
            R.id.page_my -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MyFragment()).commitAllowingStateLoss()
                return true
            }
        }

        return false
    }

}